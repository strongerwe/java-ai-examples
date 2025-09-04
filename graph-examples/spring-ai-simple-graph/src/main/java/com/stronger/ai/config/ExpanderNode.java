package com.stronger.ai.config;


import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.action.NodeAction;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.PromptTemplate;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author qiang.w
 * @version release-1.0.0
 * @class ExpanderNode.class
 * @department Platform R&D
 * @date 2025/9/3
 * @desc do what?
 */
public class ExpanderNode implements NodeAction {

    private static final String DEFAULT_TEMPLATE =  "你是信息检索和搜索优化方面的专家。\n您的任务是生成给定查询的{number}个不同版本。\n\n每个变体必须涵盖主题的不同观点或方面，同时保持原始查询的核心意图。目标是扩大搜索空间，提高找到相关信息的机会。\n不要解释你的选择或添加任何其他文字。\n提供以换行符分隔的查询变体。\n\n原始查询：{query}\n\n查询变量:";

    private static final String DEFAULT_TEMPLATE_EN = "You are an expert at information retrieval and search optimization.\nYour task is to generate {number} different versions of the given query.\n\nEach variant must cover different perspectives or aspects of the topic,\nwhile maintaining the core intent of the original query. The goal is to\nexpand the search space and improve the chances of finding relevant information.\n\nDo not explain your choices or add any other text.\nProvide the query variants separated by newlines.\n\nOriginal query: {query}\n\nQuery variants:\n";

    private static final PromptTemplate DEFAULT_PROMPT_TEMPLATE = new PromptTemplate(DEFAULT_TEMPLATE);

    private final ChatClient chatClient;

    private final Integer NUMBER = 3;

    public ExpanderNode(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    @Override
    public Map<String, Object> apply(OverAllState state) throws Exception {
        String query = state.value("query", "");
        Integer expanderNumber = state.value("expander_number", this.NUMBER);

        String result = this.chatClient.prompt().user((user) -> user.text(DEFAULT_PROMPT_TEMPLATE.getTemplate()).param("number", expanderNumber).param("query", query)).call().content();

//        Flux<String> streamResult = this.chatClient.prompt().user((user) -> user.text(DEFAULT_PROMPT_TEMPLATE.getTemplate()).param("number", expanderNumber).param("query", query)).stream().content();
//        String result = streamResult.reduce("", (acc, item) -> acc + item).block();
        List<String> queryVariants = Arrays.asList(result.split("\n"));

        HashMap<String, Object> resultMap = new HashMap<>();
        resultMap.put("expander_content", queryVariants);
        return resultMap;
    }
}
