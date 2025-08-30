package com.stronger.ai.controller;


import com.stronger.ai.mysql.MysqlChatMemoryRepository;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.Message;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.UUID;

import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;

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
    private final MysqlChatMemoryRepository mysqlChatMemoryRepository;
    /**
     * 最多保存100条消息
     */
    private static final int MAX_MESSAGES = 100;
    public ChatController(ChatClient.Builder builder, MysqlChatMemoryRepository mysqlChatMemoryRepository) {
        this.mysqlChatMemoryRepository = mysqlChatMemoryRepository;

        MessageWindowChatMemory messageWindowChatMemory =
                MessageWindowChatMemory.builder()
                        .chatMemoryRepository(mysqlChatMemoryRepository)
                        .maxMessages(MAX_MESSAGES)
                        .build();
        this.chatClient = builder
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(
                                messageWindowChatMemory
                        ).build())
                .build();
    }

    /* 当前未建立用户体系，期望每次重启都是新的对话 */
    private final static String LOCAL_CONVERSATION_ID = UUID.randomUUID().toString();

    @GetMapping("/chat/call")
    public String call(@RequestParam(value = "question", defaultValue = "你好，你是什么大模型？") String question) {
        return chatClient
                .prompt(question)
                .advisors(user -> user.param(CONVERSATION_ID, LOCAL_CONVERSATION_ID))
                .call()
                .content();
    }

    @GetMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream(@RequestParam(value = "question",
            defaultValue = "你好，能否用中文给我讲一个小笑话？") String question) {
        return chatClient.prompt(question)
                .advisors(user -> user.param(CONVERSATION_ID, LOCAL_CONVERSATION_ID))
                .stream()
                .content();
    }

    /**
     * 获取上下文消息
     *
     * @return {@link List }<{@link Message }>
     */
    @GetMapping("/chat/messages")
    public List<Message> messages() {
        return mysqlChatMemoryRepository.findByConversationId(LOCAL_CONVERSATION_ID);
    }
}
