package com.stronger.ai;


import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @author qiang.w
 * @version release-1.0.0
 * @class SpringAiSimpleMcpServerApplication.class
 * @department Platform R&D
 * @date 2025/9/2
 * @desc 启动类
 */
@SpringBootApplication
public class SpringAiSimpleMcpServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(SpringAiSimpleMcpServerApplication.class, args);
    }

    @Bean
    public ToolCallbackProvider timeTools(ParamMcpService paramMcpService) {
        return MethodToolCallbackProvider.builder().toolObjects(paramMcpService).build();
    }
}
