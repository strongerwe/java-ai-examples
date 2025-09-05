package com.stronger.ai;


import dev.langchain4j.community.store.embedding.redis.RedisEmbeddingStore;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.loader.ClassPathDocumentLoader;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.rag.content.retriever.EmbeddingStoreContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author qiang.w
 * @version release-1.0.0
 * @class RagChatConfiguration.class
 * @department Platform R&D
 * @date 2025/9/1
 * @desc do what?
 */
@Configuration
public class RagChatConfiguration {

    /**
     * 构建向量数据库检索对象
     * <p>
     * //     * @param embeddingStore embeddingStore
     *
     * @return {@link ContentRetriever }
     */
    @Bean("contentRetriever")
    public ContentRetriever contentRetriever(RedisEmbeddingStore redisEmbeddingStore,
                                             EmbeddingModel embeddingModel) {
        return EmbeddingStoreContentRetriever
                .builder()
                .embeddingStore(embeddingStore(redisEmbeddingStore, embeddingModel))
                .embeddingModel(embeddingModel)
                .maxResults(1)
                .minScore(0.5)
                .build();
    }

    /**
     * 构建向量数据库操作对象
     *
     * @return {@link EmbeddingStore }<{@link TextSegment }>
     */
    public EmbeddingStore<TextSegment> embeddingStore(
            RedisEmbeddingStore redisEmbeddingStore,
            EmbeddingModel embeddingModel) {
        // 1.从指定目录加载文档到内存
        List<Document> documents =
                // 默认加载md文件
                ClassPathDocumentLoader.loadDocuments("rag");
        // 3.构建文档分割器对象：500.最大分片长度  100.两个片段最大重复字符
        DocumentSplitter documentSplitter = DocumentSplitters.recursive(
                500, 100, null
        );

        // 4.构建一个EmbeddingStoreIngestor对象，完成文本数据的切割，向量化，存储
        EmbeddingStoreIngestor ingestor = EmbeddingStoreIngestor.builder()
                .embeddingStore(redisEmbeddingStore)
                .documentSplitter(documentSplitter)
                .embeddingModel(embeddingModel)
                .build();
        ingestor.ingest(documents);
        return redisEmbeddingStore;
    }


}
