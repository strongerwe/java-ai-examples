package com.stronger.ai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author qiang.w
 * @version release-1.0.0
 * @class ChatConfiguration.class
 * @department Platform R&D
 * @date 2025/8/27
 * @desc do what?
 */
@Slf4j
@Configuration
public class ChatMcpConfiguration {

    @Bean
    public ChatClient chatClient(ChatClient.Builder chatClientBuilder,
                                 @Qualifier("loadbalancedMcpAsyncToolCallbacks") ToolCallbackProvider toolCallbackProvider) {
        ToolCallback[] toolCallbacks = toolCallbackProvider.getToolCallbacks();
        log.info("注册Mcp数量|：{}", toolCallbacks.length);
        for (ToolCallback toolCallback : toolCallbacks) {
            log.info("Mcp[{}]|{}|{}", toolCallback.getToolDefinition().name(), toolCallback.getToolDefinition().description(), toolCallback.getToolDefinition().inputSchema());
        }
        return chatClientBuilder
                .defaultToolCallbacks(toolCallbacks)
                .build();
    }
}
