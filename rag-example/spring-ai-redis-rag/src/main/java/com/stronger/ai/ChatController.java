package com.stronger.ai;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.redis.RedisVectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qiang.w
 * @version release-1.0.0
 * @class ChatController.class
 * @department Platform R&D
 * @date 2025/8/29
 * @desc chat接口控制类
 */
@RestController
public class ChatController {

    private final ChatClient chatClient;
    private final RedisVectorStore redisVectorStore;

    public ChatController(ChatClient.Builder builder, EmbeddingModel embeddingModel,
                          @Qualifier("redisVectorStoreCustom") RedisVectorStore redisVectorStore) {
        this.redisVectorStore = redisVectorStore;
        /* 定义ragAdvisor */
        RetrievalAugmentationAdvisor ragAdvisor = RetrievalAugmentationAdvisor.builder()
                .documentRetriever(
                        VectorStoreDocumentRetriever
                                .builder()
                                .vectorStore(redisVectorStore)
                                .build())
                .build();
        this.chatClient = builder
                .defaultAdvisors(ragAdvisor)
                .build();
        /* 读取markdown文件内容加载进向量数据库 */
        var markdownReader1 = new MarkdownDocumentReader("classpath:rag/readme.md", MarkdownDocumentReaderConfig.builder().build());
        List<Document> documents = new ArrayList<>(markdownReader1.get());
        redisVectorStore.add(documents);
    }

    @PostMapping("/rag/add")
    public void add() {
        List<Document> documents = List.of(
                new Document("你的姓名是stronger，江西人，15年毕业的Java攻城狮，拥有10年软件开发的工作经验，目前在浙江省杭州市工作。"),
                new Document("你的姓名是stronger，你的兴趣爱好是：打篮球、跑步、旅游以及唱歌。"));
        redisVectorStore.add(documents);
    }

    @GetMapping("/search")
    public List<Document> search() {
        return redisVectorStore.similaritySearch(SearchRequest
                .builder()
                .query("stronger")
                .topK(2)
                .build());
    }

    @GetMapping("/chat/call")
    public String call(@RequestParam(value = "question", defaultValue = "你好，请你介绍一下stronger这个人的信息") String question) {
        return chatClient.prompt(question).call().content();
    }

    @GetMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream(@RequestParam(value = "question",
            defaultValue = "您好，请您介绍一下stronger这个人的信息") String question) {
//        /* 定义ragAdvisor */
//        RetrievalAugmentationAdvisor ragAdvisor = RetrievalAugmentationAdvisor.builder()
//                .documentRetriever(
//                        VectorStoreDocumentRetriever
//                                .builder()
//                                .vectorStore(redisVectorStore)
//                                .build())
//                .build();
        return chatClient
                .prompt(question)
//                .advisors(ragAdvisor)
                .stream()
                .content();
    }
}
