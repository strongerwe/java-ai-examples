package com.stronger.ai;


import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

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

    public ChatController(OpenAiChatModel openAiChatModel,
                          AiChatService aiChatService) {
        this.openAiChatModel = openAiChatModel;
        this.aiChatService = aiChatService;
    }

    @GetMapping("/chat/call")
    public String call(@RequestParam(value = "question", defaultValue = "你好，你是什么大模型？") String question) {
        return openAiChatModel.chat(question);
    }

    @GetMapping("/chat/call2")
    public String call2(@RequestParam(value = "question", defaultValue = "你好，你是什么大模型？") String question) {
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
