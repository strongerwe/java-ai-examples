package con.stronger.ai;


import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.TokenCountBatchingStrategy;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.ai.vectorstore.elasticsearch.ElasticsearchVectorStore;
import org.springframework.ai.vectorstore.elasticsearch.ElasticsearchVectorStoreOptions;
import org.springframework.ai.vectorstore.elasticsearch.SimilarityFunction;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qiang.w
 * @version release-1.0.0
 * @class EsConfiguration.class
 * @department Platform R&D
 * @date 2025/9/1
 * @desc Es配置bean，初始化向量数据库
 */
@Slf4j
@Configuration
public class EsConfiguration {

    @Value("${spring.elasticsearch.uris}")
    private String url;
    @Value("${spring.elasticsearch.username}")
    private String username;
    @Value("${spring.elasticsearch.password}")
    private String password;

    @Value("${spring.ai.vectorstore.elasticsearch.index-name}")
    private String indexName;
    @Value("${spring.ai.vectorstore.elasticsearch.similarity}")
    private SimilarityFunction similarityFunction;
    @Value("${spring.ai.vectorstore.elasticsearch.dimensions}")
    private int dimensions;


    @Bean
    public RestClient restClient() {
        // 解析URL
        String[] urlParts = url.split("://");
        String protocol = urlParts[0];
        String hostAndPort = urlParts[1];
        String[] hostPortParts = hostAndPort.split(":");
        String host = hostPortParts[0];
        int port = Integer.parseInt(hostPortParts[1]);

        // 创建凭证提供者
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        credentialsProvider.setCredentials(AuthScope.ANY,
                new UsernamePasswordCredentials(username, password));
        log.info("create elasticsearch rest client");
        // 构建RestClient
        return RestClient.builder(new HttpHost(host, port, protocol))
                .setHttpClientConfigCallback(httpClientBuilder -> {
                    httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                    return httpClientBuilder;
                })
                .build();
    }

    @Bean
    @Qualifier("elasticsearchVectorStore")
    public ElasticsearchVectorStore vectorStore(RestClient restClient, EmbeddingModel embeddingModel) {
        log.info("create elasticsearch vector store");
        ElasticsearchVectorStoreOptions options = new ElasticsearchVectorStoreOptions();
        // Optional: defaults to "spring-ai-document-index"
        options.setIndexName(indexName);
        // Optional: defaults to COSINE
        options.setSimilarity(similarityFunction);
        // Optional: defaults to model dimensions or 1536
        options.setDimensions(dimensions);
        ElasticsearchVectorStore elasticsearchVectorStore = ElasticsearchVectorStore.builder(restClient, embeddingModel)
                // Optional: use custom options
                .options(options)
                // Optional: defaults to false
                .initializeSchema(true)
                // Optional: defaults to TokenCountBatchingStrategy
                .batchingStrategy(new TokenCountBatchingStrategy())
                .build();

        /* 读取markdown文件内容加载进向量数据库 */
        var markdownReader1 = new MarkdownDocumentReader("classpath:rag/readme.md", MarkdownDocumentReaderConfig.builder().build());
        List<Document> documents = new ArrayList<>(markdownReader1.get());
        elasticsearchVectorStore.add(documents);
        return elasticsearchVectorStore;
    }
}
