package com.stronger.ai;


import io.micrometer.common.util.StringUtils;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author qiang.w
 * @version release-1.0.0
 * @class ParamController.class
 * @department Platform R&D
 * @date 2025/9/3
 * @desc do what?
 */
@Slf4j
@RestController
public class ParamController {

    @GetMapping("/param/search")
    @Operation(summary = "根据参数Key查询参数值")
    public String search(@RequestParam("key") String key) {
        log.info("ParamMcpService.apply|输入：{}", key);
        String param = this.getParam(key);
        if (StringUtils.isNotBlank(param)) {
            return param;
        }
        return "抱歉：未查询到系统参数！";
    }

    @GetMapping("/param/update")
    @Operation(summary = "根据参数Key更新参数值")
    public String update(
            @RequestParam("paramKey")
            String paramKey,
            @RequestParam("paramValue")
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
