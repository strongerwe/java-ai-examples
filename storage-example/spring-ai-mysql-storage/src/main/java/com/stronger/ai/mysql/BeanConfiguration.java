package com.stronger.ai.mysql;


import jakarta.annotation.Resource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

/**
 * @author qiang.w
 * @version release-1.0.0
 * @class BeanConfiguration.class
 * @department Platform R&D
 * @date 2025/8/29
 * @desc do what?
 */
@Configuration
public class BeanConfiguration {

    @Resource
    private ParamConfig paramConfig;

    /**
     * mysql 存储
     *
     * @return {@link MysqlChatMemoryRepository }
     */
    @Bean
    public MysqlChatMemoryRepository mysqlChatMemoryRepository() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName(paramConfig.getMysqlDriverClassName());
        dataSource.setUrl(paramConfig.getMysqlJdbcUrl());
        dataSource.setUsername(paramConfig.getMysqlUsername());
        dataSource.setPassword(paramConfig.getMysqlPassword());
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return MysqlChatMemoryRepository.mysqlBuilder()
                .jdbcTemplate(jdbcTemplate)
                .build();
    }
}
