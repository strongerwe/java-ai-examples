package com.stronger.ai;


import com.fasterxml.jackson.annotation.JsonClassDescription;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyDescription;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

import java.util.function.Function;

/**
 * @author qiang.w
 * @version release-1.0.0
 * @class TimeTools.class
 * @department Platform R&D
 * @date 2025/9/1
 * @desc do what?
 */
@Service
@Slf4j
@NoArgsConstructor
public class TimeTools implements Function<TimeTools.Request, TimeTools.Response> {

    /**
     * 获取当前时间Tools
     *
     * @param timeZoneId 查询的时区（默认时区为北京时间）
     * @return {@link String }
     */
    @Tool(description = "获取指定城市的时间，请用中文显示格式直接回答我，参考格式为：现在是北京时间：YYYY年MM月dd日 HH点mm分ss秒")
    public String getCityTime(
            @ToolParam(description = "Time zone id, such as Asia/Shanghai")
            String timeZoneId) {
        log.info("LocalTimeTool.getCityTime|根据时区获取时间Tool|{}", timeZoneId);
        String description = apply(new Request(timeZoneId)).description();
        log.info("LocalTimeTool.getCityTime|响应：{}", description);
        return description;
    }

    @Override
    public Response apply(Request request) {
        String timeZoneId = request.timeZoneId;
        return new Response(String.format("The current time zone is %s and the current time is %s", timeZoneId, ZoneUtils.getTimeByZoneId(timeZoneId)));
    }

    public record Response(String description) {
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    @JsonClassDescription("Get the current time based on time zone id")
    public record Request(String timeZoneId) {
        public Request(@JsonProperty(required = true, value = "timeZoneId") @JsonPropertyDescription("Time zone id, such as Asia/Shanghai") String timeZoneId) {
            this.timeZoneId = timeZoneId;
        }

        @JsonProperty(
                required = true,
                value = "timeZoneId"
        )
        @JsonPropertyDescription("Time zone id, such as Asia/Shanghai")
        public String timeZoneId() {
            return this.timeZoneId;
        }
    }
}
