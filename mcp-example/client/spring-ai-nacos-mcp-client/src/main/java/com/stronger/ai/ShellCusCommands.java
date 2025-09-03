package com.stronger.ai;


import jakarta.annotation.Resource;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

/**
 * @author qiang.w
 * @version release-1.0.0
 * @class ShellCusCommands.class
 * @department Platform R&D
 * @date 2025/9/2
 * @desc do what?
 */
@ShellComponent
public class ShellCusCommands {

    @Resource
    private ChatClient chatClient;

    /**
     * 流式打印到控制的回答
     * （这里不进行上下文存储）
     *
     * @param q 提问
     */
    @ShellMethod(key = "chat", group = "AI提问命令")
    public void chat(@ShellOption(defaultValue = "你好，帮我查询一下key2的参数值") String q) {
        System.out.println("提问问题：\n\t" + q);
        System.out.println("AI回答：\t");
        // 实时输出
        chatClient.prompt(q)
                .system("""
                        
                        系统管理员，请以友好、乐于主任且愉快的方式来回复，
                        在线聊天系统与用户互动。
                        
                        """)
                .stream()
                .content()
                .doOnNext(System.out::print)
                // 阻塞等待所有数据接收完成
                .blockLast();
        System.out.println();
    }
}
