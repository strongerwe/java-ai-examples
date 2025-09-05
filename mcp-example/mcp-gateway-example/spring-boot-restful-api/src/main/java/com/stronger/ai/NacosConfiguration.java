package com.stronger.ai;


import com.alibaba.nacos.api.PropertyKeyConst;
import com.alibaba.nacos.api.exception.NacosException;
import com.alibaba.nacos.api.naming.NamingFactory;
import com.alibaba.nacos.api.naming.NamingService;
import com.alibaba.nacos.api.naming.pojo.Instance;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * @author qiang.w
 * @version release-1.0.0
 * @class NacosConfiguration.class
 * @department Platform R&D
 * @date 2025/9/3
 * @desc do what?
 */
@Slf4j
@Configuration
public class NacosConfiguration {

    @Value("${spring.application.name}")
    private String serviceName;
    @Value("${spring.cloud.nacos.discovery.server-addr}")
    private String serverAddr;
    @Value("${spring.cloud.nacos.discovery.namespace}")
    private String namespace;
    @Value("${spring.cloud.nacos.discovery.username}")
    private String username;
    @Value("${spring.cloud.nacos.discovery.password}")
    private String password;

    @Value("${server.port}")
    private int port;

    @Bean
    public NamingService namingService() throws NacosException, UnknownHostException {
        Properties properties = new Properties();
        properties.put(PropertyKeyConst.NAMESPACE, Objects.toString(this.namespace, ""));
        properties.put(PropertyKeyConst.SERVER_ADDR, Objects.toString(this.serverAddr, ""));
        properties.put(PropertyKeyConst.USERNAME, Objects.toString(this.username, ""));
        properties.put(PropertyKeyConst.PASSWORD, Objects.toString(this.password, ""));

        NamingService namingService = NamingFactory.createNamingService(properties);
        init(namingService);
        return namingService;
    }

    private void init(NamingService namingService) throws NacosException, UnknownHostException {
        Instance instance = new Instance();
        // 自动获取本机IP
        instance.setIp(InetAddress.getLocalHost().getHostAddress());
        instance.setPort(port);
        instance.setMetadata(Map.of("register.timestamp", String.valueOf(System.currentTimeMillis())));
        log.info("注册实例: {}:{}|{}", instance.getIp(), port, serviceName);
        namingService.registerInstance(serviceName, instance);
    }
}
