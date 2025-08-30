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
     * mysql 存储（这里参考spring-ai-alibaba-starter-memory-jdbc手写了一遍）
     * <p>
     * 如果想要自己重新设计数据库存储表ai_chat_memory这里可以参考重写一份，源码很简单就是对应的curd
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

    /**
     * 阿里巴巴mysql存储 (这里是直接使用：spring-ai-alibaba-starter-memory-jdbc)
     *
     * @return {@link com.alibaba.cloud.ai.memory.jdbc.MysqlChatMemoryRepository }
     */
//    @Bean
//    public com.alibaba.cloud.ai.memory.jdbc.MysqlChatMemoryRepository alMysqlChatMemoryRepository() {
//        DriverManagerDataSource dataSource = new DriverManagerDataSource();
//        dataSource.setDriverClassName(paramConfig.getMysqlDriverClassName());
//        dataSource.setUrl(paramConfig.getMysqlJdbcUrl());
//        dataSource.setUsername(paramConfig.getMysqlUsername());
//        dataSource.setPassword(paramConfig.getMysqlPassword());
//        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
//        return com.alibaba.cloud.ai.memory.jdbc.MysqlChatMemoryRepository.mysqlBuilder()
//                .jdbcTemplate(jdbcTemplate)
//                .build();
//    }
}
