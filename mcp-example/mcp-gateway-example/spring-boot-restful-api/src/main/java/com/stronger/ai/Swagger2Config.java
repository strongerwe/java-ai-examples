package com.stronger.ai;


import io.swagger.v3.oas.models.ExternalDocumentation;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author qiang.w
 * @version release-1.0.0
 * @class Swagger2Config.class
 * @department Platform R&D
 * @date 2025/9/3
 * @desc do what?
 */
@Configuration
@EnableConfigurationProperties(Swagger2Config.SwaggerConfigProperties.class)
public class Swagger2Config {

    @Bean
    public OpenAPI springShopOpenAPI() {
        return new OpenAPI()
                .info(new Info().title("标题")
                        .description("我的API文档")
                        .version("v1")
                        .license(new License().name("Apache 2.0").url("http://springdoc.org")))
                .externalDocs(new ExternalDocumentation());
    }

    @ConfigurationProperties(prefix = "springdoc.swagger-ui")
    public static class SwaggerConfigProperties {
        private String path;

        public String getPath() {
            return path;
        }

        public void setPath(String path) {
            this.path = path;
        }
    }
}
