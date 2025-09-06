package com.stronger.ai;


import dev.langchain4j.agent.tool.P;
import dev.langchain4j.agent.tool.Tool;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * @author qiang.w
 * @version release-1.0.0
 * @class CustomTools.class
 * @department Platform R&D
 * @date 2025/9/6
 * @desc do what?
 */
@Slf4j
@Component
public class CustomTools {

    @Tool("挂号预约")
    public void reservation(@P("姓名") String name,
                            @P("身份证号") String idenNum,
                            @P("挂号科室") String deptName,
                            @P("就诊时间") String datetime) {
        // 这里我属于偷懒了，实际应该调用预约挂号的API接口
        log.info("预约挂号成功，姓名：{}，身份证号：{}，挂号科室：{}，就诊时间：{}", name, idenNum, deptName, datetime);
    }

    @Tool("查询可挂号科室")
    public String findDept(@P("日期") String date) {
        log.info("查询可挂号科室，日期：{}", date);
        // 这里也是偷懒了，应该查询可挂号排班
        return "当前可挂号科室：消化内科-2025年9月7日上午10:00-11:00";
    }
}
