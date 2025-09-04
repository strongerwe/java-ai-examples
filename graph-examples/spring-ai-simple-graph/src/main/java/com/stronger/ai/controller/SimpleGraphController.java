package com.stronger.ai.controller;


import com.alibaba.cloud.ai.graph.CompiledGraph;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import com.alibaba.cloud.ai.graph.exception.GraphStateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * @author qiang.w
 * @version release-1.0.0
 * @class SimpleGraphController.class
 * @department Platform R&D
 * @date 2025/9/3
 * @desc do what?
 */
@Slf4j
@RestController
public class SimpleGraphController {

    private final CompiledGraph compiledGraph;

    public SimpleGraphController(@Qualifier("simpleGraph") StateGraph stateGraph) throws GraphStateException {
        this.compiledGraph = stateGraph.compile();
    }

    @GetMapping(value = "/graph/expand")
    public Map<String, Object> expand(@RequestParam(value = "query", defaultValue = "你好，很高兴认识你，能简单介绍一下自己吗？", required = false) String query,
                                      @RequestParam(value = "expander_number", defaultValue = "3", required = false) Integer expanderNumber,
                                      @RequestParam(value = "thread_id", defaultValue = "stronger", required = false) String threadId) throws GraphRunnerException {
        RunnableConfig runnableConfig = RunnableConfig.builder().threadId(threadId).build();
        Map<String, Object> objectMap = new HashMap<>();
        objectMap.put("query", query);
        objectMap.put("expander_number", expanderNumber);
        Optional<OverAllState> invoke = this.compiledGraph.invoke(objectMap, runnableConfig);
        return invoke.map(OverAllState::data).orElse(new HashMap<>());
    }
}
