package com.jollychic.holmes.config;

import com.google.common.collect.Sets;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Created by WIN7 on 2018/1/19.
 */
@Configuration
@EnableSwagger2
public class SwaggerConfig {
    public static final String SWAGGER_SCAN_BASE_PACKAGE = "com.jollychic.holmes.controller";
    public static final String VERSION = "1.0";

    ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Holmes API")
                .description("This is to show api description")
                .version(VERSION)
                .build();
    }

    @Bean
    public Docket customImplementation(){
        return new Docket(DocumentationType.SWAGGER_2)
                .select()
                .apis(RequestHandlerSelectors.basePackage(SWAGGER_SCAN_BASE_PACKAGE))
                .build()
                .produces(Sets.newHashSet("application/json;charset=UTF-8"))
                .consumes(Sets.newHashSet("application/json;charset=UTF-8"))
                .apiInfo(apiInfo());
    }
}
