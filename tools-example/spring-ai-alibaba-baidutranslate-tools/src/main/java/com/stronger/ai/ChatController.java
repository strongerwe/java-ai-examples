package com.stronger.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.http.MediaType;
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

    private final ChatClient chatClient;

    public ChatController(ChatModel chatModel) {
        this.chatClient = ChatClient.builder(chatModel)
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .build();
    }

    @GetMapping("/chat/call")
    public String call(@RequestParam(value = "question", defaultValue = "帮我把以下内容翻译成英文：你好，世界。") String question) {
        return chatClient.prompt(question)
                .toolNames("baiduTranslate")
                .call().content();
    }

    @GetMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream(@RequestParam(value = "question",
            defaultValue = "帮我把以下内容翻译成英文：你好，世界。") String question) {
        return chatClient.prompt(question)
                .toolNames("baiduTranslate")
                .stream().content();
    }
}
