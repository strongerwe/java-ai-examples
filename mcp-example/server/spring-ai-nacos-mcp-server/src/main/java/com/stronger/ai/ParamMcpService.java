package com.stronger.ai;


import io.micrometer.common.util.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author qiang.w
 * @version release-1.0.0
 * @class ParamUpdateMcpService.class
 * @department Platform R&D
 * @date 2025/8/25
 * @desc 参数Mcp服务
 */
@Slf4j
@Service
public class ParamMcpService {


    @Tool(name = "paramSearch", description = "根据参数Key查询参数值")
    public String search(@ToolParam(description = "参数Key") String input) {
        log.info("ParamMcpService.apply|输入：{}", input);
        String param = this.getParam(input);
        if (StringUtils.isNotBlank(param)) {
            return param;
        }
        return "抱歉：未查询到系统参数！";
    }

    @Tool(name = "paramUpdate", description = "根据参数Key更新参数值")
    public String update(
            @ToolParam(description = "参数Key")
            String paramKey,

            @ToolParam(description = "参数值")
            String paramValue) {
        log.info("ParamUpdateMcpService.apply|参数Key:{},参数值:{}", paramKey, paramValue);
        String param = this.getParam(paramKey);
        if (StringUtils.isNotBlank(param)) {
            this.setParam(paramKey, paramValue);
            return "更新成功！";
        }
        return "抱歉：未查询到系统参数[" + paramKey + "]的配置！";
    }

    private final static Map<String, String> PARAM_POOL = new ConcurrentHashMap<>();

    static {
        Map<String, String> mockData = Map.of(
                "key1", "val1",
                "key2", "val2",
                "key3", "val3",
                "百度地址", "https://baidu.com",
                "北京", "中华人民共和国首都",
                "杭州", "中华人民共和国浙江省省会"
        );
        PARAM_POOL.putAll(mockData);
    }

    private String getParam(String key) {
        return PARAM_POOL.get(key);
    }

    private void setParam(String key, String value) {
        PARAM_POOL.put(key, value);
    }
}
