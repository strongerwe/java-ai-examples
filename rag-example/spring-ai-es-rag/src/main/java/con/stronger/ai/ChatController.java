package con.stronger.ai;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.document.Document;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.elasticsearch.ElasticsearchVectorStore;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

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
    private final ElasticsearchVectorStore elasticsearchVectorStore;

    public ChatController(ChatClient.Builder builder,
                          ElasticsearchVectorStore elasticsearchVectorStore) {
        this.chatClient = builder.build();
        this.elasticsearchVectorStore = elasticsearchVectorStore;
    }

    @PostMapping("/rag/add")
    public void add() {
        List<Document> documents = List.of(
                new Document("你的姓名是stronger，江西人，15年毕业的Java攻城狮，拥有10年软件开发的工作经验，目前在浙江省杭州市工作。"),
                new Document("你的姓名是stronger，你的兴趣爱好是：打篮球、跑步、旅游以及唱歌。"));
        elasticsearchVectorStore.add(documents);
    }

    @GetMapping("/chat/call")
    public String call(@RequestParam(value = "question", defaultValue = "你好，请你介绍一下stronger这个人的信息") String question) {
        return chatClient.prompt(question).call().content();
    }

    @GetMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream(@RequestParam(value = "question",
            defaultValue = "您好，请您介绍一下stronger这个人的信息") String question) {
        /* 定义ragAdvisor */
        RetrievalAugmentationAdvisor ragAdvisor = RetrievalAugmentationAdvisor.builder()
                .documentRetriever(
                        VectorStoreDocumentRetriever
                                .builder()
                                .vectorStore(elasticsearchVectorStore)
                                .build())
                .build();
        return chatClient
                .prompt(question)
                .advisors(ragAdvisor)
                .stream()
                .content();
    }
}
