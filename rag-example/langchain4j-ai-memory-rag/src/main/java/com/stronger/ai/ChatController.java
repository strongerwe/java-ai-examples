package com.stronger.ai;


import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.rag.content.retriever.ContentRetriever;
import dev.langchain4j.store.embedding.EmbeddingStore;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.stream.Collectors;

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

    /**
     * 这里是两种方式
     * openAiChatModel 直接用chatModel调用
     */
    private final OpenAiChatModel openAiChatModel;

    /**
     * aiChatService基于@AiService注解生成的服务类
     */
    private final AiChatService aiChatService;

    private final EmbeddingStore<TextSegment> embeddingStore;

    private final EmbeddingModel embeddingModel;

    public ChatController(OpenAiChatModel openAiChatModel,
                          AiChatService aiChatService,
                          EmbeddingStore<TextSegment> embeddingStore,
                          ContentRetriever contentRetriever,
                          EmbeddingModel embeddingModel) {
        this.openAiChatModel = openAiChatModel;
        this.aiChatService = aiChatService;
        this.embeddingStore = embeddingStore;
        this.embeddingModel = embeddingModel;
    }

    @PostMapping("/rag/add")
    public void add() {
        List<Document> documents = List.of(
                Document.from("你的姓名是stronger，江西人，15年毕业的Java攻城狮，拥有10年软件开发的工作经验，目前在浙江省杭州市工作。"),
                Document.from("你的姓名是stronger，你的兴趣爱好是：打篮球、跑步、旅游以及唱歌。"));
        List<TextSegment> segments = documents.stream().map(Document::toTextSegment).collect(Collectors.toList());
        Response<List<Embedding>> embeddingsResponse = this.embeddingModel.embedAll(segments);
        embeddingStore.addAll(embeddingsResponse.content(), segments);
    }

    @GetMapping("/chat/call")
    public String call(@RequestParam(value = "question", defaultValue = "自定义报表中统计报表如何配置？") String question) {
        return openAiChatModel.chat(question);
    }

    @GetMapping("/chat/call2")
    public String call2(@RequestParam(value = "question", defaultValue = "自定义报表中统计报表如何配置？") String question) {
        return aiChatService.chat(question);
    }

    @GetMapping(value = "/chat/stream", produces = "text/event-stream")
    public Flux<String> stream(@RequestParam(value = "question",
            defaultValue = "你好，能否用中文给我讲一个小笑话？") String question) {
        Flux<String> response = aiChatService.streamChat(question);
        StringBuilder aiResponseBuffer = new StringBuilder();
        return response.map(a -> {
            aiResponseBuffer.append(a);
            return a;
        }).doOnComplete(() -> {
            // 打印stream输出
            System.out.println("[" + question + "]问题大模型完整回复结果：\n" + aiResponseBuffer);
        });
    }
}
