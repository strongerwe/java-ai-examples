package com.stronger.ai;


import com.alibaba.cloud.ai.memory.redis.JedisRedisChatMemoryRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * @author qiang.w
 * @version release-1.0.0
 * @class SpringAiRedisStorageApplication.class
 * @department Platform R&D
 * @date 2025/8/30
 * @desc 启动类
 */
@SpringBootApplication
public class SpringAiRedisStorageApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAiRedisStorageApplication.class, args);
    }

    @Value("${spring.ai.memory.redis.host}")
    private String redisHost;
    @Value("${spring.ai.memory.redis.port}")
    private int redisPort;
    @Value("${spring.ai.memory.redis.password}")
    private String redisPassword;
    @Value("${spring.ai.memory.redis.timeout}")
    private int redisTimeout;

    @Bean
    public JedisRedisChatMemoryRepository redisChatMemoryRepository() {
        return JedisRedisChatMemoryRepository.builder()
                .host(redisHost)
                .port(redisPort)
                // 若没有设置密码则注释该项
                .password(redisPassword)
                .timeout(redisTimeout)
                .build();
    }
}
