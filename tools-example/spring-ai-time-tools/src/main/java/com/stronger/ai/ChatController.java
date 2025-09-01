package com.stronger.ai;


import org.springframework.ai.chat.client.ChatClient;
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

    private final TimeTools timeTools;


    public ChatController(ChatClient.Builder builder, TimeTools timeTools) {
        this.chatClient = builder
//                .defaultTools(timeTools)
                .build();
        this.timeTools = timeTools;
    }

    @GetMapping("/chat/call")
    public String call(@RequestParam(value = "question", defaultValue = "你好，现在是什么时候？") String question) {
        return chatClient.prompt(question).tools(timeTools).call().content();
    }

    @GetMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream(@RequestParam(value = "question",
            defaultValue = "你好，能否用中文给我讲一个小笑话？") String question) {
        return chatClient.prompt(question).tools(timeTools).stream().content();
    }
}
