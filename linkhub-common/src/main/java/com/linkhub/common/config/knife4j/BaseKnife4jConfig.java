package com.linkhub.common.config.knife4j;

import org.springframework.context.annotation.Bean;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseKnife4jConfig {
    @Bean
    public Docket createRestApi() {
        SwaggerProperties properties = swaggerProperties();
        Docket docket = new Docket(DocumentationType.OAS_30)
                //设置简介信息
                .apiInfo(apiInfo(properties))
                //设置哪些接口暴露给Swagger展示
                .select()
                //这里指定接口Controller路径，扫描所有有注解的api，用这种方式更灵活
                //.apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .apis(RequestHandlerSelectors.basePackage(properties.getApiBasePackage()))
                .paths(PathSelectors.any())
                .build();
        if (properties.isEnableSecurity()) {
            docket.securitySchemes(securitySchemes()).securityContexts(securityContexts());
        }
        return docket;
    }

    /**
     * 创建简介信息
     * @return apiInfo
     */
    private ApiInfo apiInfo(SwaggerProperties properties){
        // 用ApiInfoBuilder进行定制简介
        return new ApiInfoBuilder()
                //标题
                .title(properties.getTitle())
                //描述
                .description(properties.getDescription())
                //作者信息
                .contact(new Contact(properties.getContactName(), properties.getContactUrl(), properties.getContactEmail()))
                //版本
                .version(properties.getVersion())
                .build();
    }

    /**
     * security配置，这里指定token通过Authorization头请求头传递
     */
    private List<SecurityScheme> securitySchemes() {
        //设置请求头信息
        List<SecurityScheme> result = new ArrayList<>();
        ApiKey apiKey = new ApiKey("Authorization", "Authorization", "header");
        result.add(apiKey);
        return result;
    }
    /**
     * 安全上下文
     */
    private List<SecurityContext> securityContexts(){
        List<SecurityContext> securityContexts = new ArrayList<>();
        securityContexts.add(
                SecurityContext.builder()
                        .securityReferences(defaultAuth())
                        .operationSelector(o ->o.requestMappingPattern().matches("/.*"))
                        .build()
        );
        return securityContexts;
    }
    /**
     * 默认安全引用
     */
    private List<SecurityReference> defaultAuth() {
        List<SecurityReference> securityReferences = new ArrayList<>();
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
        authorizationScopes[0] = authorizationScope;
        securityReferences.add(new SecurityReference("Authorization",authorizationScopes));
        return securityReferences;
    }

    /**
     * 自定义swagger配置，子类需要重新该方法
     */
    public abstract SwaggerProperties swaggerProperties();
}
