package com.stronger.ai;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.Map;

/**
 * @author qiang.w
 * @version release-1.0.0
 * @class ChatController.class
 * @department Platform R&D
 * @date 2025/8/29
 * @desc chat接口控制类
 */
@Slf4j
@RestController
public class ChatController {

    @Value("classpath:/prompts/translation-prompt.st")
    private Resource markdownPromptResource;

    private final ChatClient chatClient;

    public ChatController(OllamaChatModel ollamaChatModel) {
        this.chatClient = ChatClient.builder(ollamaChatModel)
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .defaultOptions(OllamaOptions.builder()
                        /* 采样温度，控制模型生成文本的多样性。temperature越高，
                        生成的文本更多样，反之，生成的文本更确定。
                        取值范围： [0, 2)由于temperature与top_p均可以控制生成文本的多样性，因此建议您只设置其中一个值。 */
                        .temperature(0.0)
                        .build())
                .build();
    }

    @GetMapping("/chat/call")
    public String call(@RequestParam(value = "text", defaultValue = "你好，世界！") String text,
                       @RequestParam(value = "sourceLanguage", defaultValue = "中文") String sourceLanguage,
                       @RequestParam(value = "targetLanguage", defaultValue = "英文") String targetLanguage) {
        log.info("用户提问：{}", text);

        // 1. 加载Prompt模板
        PromptTemplate promptTemplate = new PromptTemplate(markdownPromptResource);
        Map<String, Object> params = Map.of(
                "sourceLanguage", sourceLanguage,
                "targetLanguage", targetLanguage,
                "markdownContent", text
        );

        // 2. 构建翻译Prompt
        Prompt prompt = new Prompt(
                String.valueOf(promptTemplate.create(params))
        );

        String content = chatClient.prompt(prompt).call().content();
        log.info("AI回答：{}", content);
        return content;
    }

    private static final String TRANSLATION_PROMPT_TEMPLATE = "你是一名专业的翻译引擎，不要掺杂任何发挥，直接返回翻译结果，请勿返回其他内容；请将以下文本从%s翻译成%s：\n\n%s";

    @GetMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> stream(@RequestParam(value = "text", defaultValue = "你好，世界！") String text,
                               @RequestParam(value = "sourceLanguage", defaultValue = "中文") String sourceLanguage,
                               @RequestParam(value = "targetLanguage", defaultValue = "英文") String targetLanguage) {
        /* 字符串拼接 */
        String prompt = String.format(TRANSLATION_PROMPT_TEMPLATE, sourceLanguage, targetLanguage, text);
        return chatClient.prompt().user(prompt).stream().content();
    }
}
