package com.stronger.ai;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author qiang.w
 * @version release-1.0.0
 * @class ChatOptionController.class
 * @department Platform R&D
 * @date 2025/8/29
 * @desc chat options配置示例接口类
 */
@RestController
public class ChatOptionController {

    private final ChatClient chatClient;

    public ChatOptionController(ChatClient.Builder builder) {
        this.chatClient = builder
                .defaultOptions(
                        OpenAiChatOptions.builder()
                                .temperature(0.9)
                                .build()
                )
                .build();
    }

    @GetMapping("/option/chat/call")
    public String call(@RequestParam(value = "question", defaultValue = "你好，你是什么大模型？") String question) {
        return chatClient.prompt(question).call().content();
    }

    @GetMapping("/option/chat/call/temperature")
    public String callOption(@RequestParam(value = "question",
            defaultValue = "你好，能否用中文给我讲一个小笑话？") String question) {
        return chatClient.prompt(question)
                .options(
                        OpenAiChatOptions.builder()
                                .temperature(0.0)
                                .build()
                )
                .call().content();
    }
}
