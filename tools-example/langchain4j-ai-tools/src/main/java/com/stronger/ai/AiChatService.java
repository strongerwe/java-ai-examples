package com.stronger.ai;


import dev.langchain4j.service.spring.AiService;
import dev.langchain4j.service.spring.AiServiceWiringMode;
import reactor.core.publisher.Flux;

/**
 * @author qiang.w
 * @version release-1.0.0
 * @interface ChatService.class
 * @department Platform R&D
 * @date 2025/8/29
 * @desc 支持注解方式创建AiService
 */
@AiService(
        wiringMode = AiServiceWiringMode.EXPLICIT,
        chatModel = "openAiChatModel",
        tools = "customTools",
        /* 流式Model */
        streamingChatModel = "openAiStreamingChatModel"
)
public interface AiChatService {
    /**
     * chat
     *
     * @param question question
     * @return {@link String }
     */
    String chat(String question);

    /**
     * 流式响应输出对话内容
     *
     * @param question question
     * @return {@link Flux }<{@link String }>
     */
    Flux<String> streamChat(String question);
}
