package com.stronger.ai.mysql;


import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @author qiang.w
 * @version release-1.0.0
 * @class ParamConfig.class
 * @department Platform R&D
 * @date 2025/8/1
 * @desc do what?
 */
@Component
@Getter
public class ParamConfig {

    @Value("${sso.login.timeout:84300}")
    private Integer loginTimeout;

    @Value("${ai.chat.max-messages:20}")
    private int contextMaxMessages;
    @Value("${spring.ai.chat.memory.repository.jdbc.mysql.jdbc-url}")
    private String mysqlJdbcUrl;
    @Value("${spring.ai.chat.memory.repository.jdbc.mysql.username}")
    private String mysqlUsername;
    @Value("${spring.ai.chat.memory.repository.jdbc.mysql.password}")
    private String mysqlPassword;
    @Value("${spring.ai.chat.memory.repository.jdbc.mysql.driver-class-name}")
    private String mysqlDriverClassName;

}
