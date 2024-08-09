package com.campus.wall.config;


import io.swagger.annotations.ApiOperation;
import org.springframework.boot.autoconfigure.web.servlet.error.BasicErrorController;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Knife4j 3.X 配置类.
 * 访问地址：
 * <p>
 *     Knife4j 访问首页：<a href="http://localhost:8081/doc.html#/home">...</a>
 * </p>
 *
 * @author MrJoe
 * @date 2022-6-27 23:43:39
 */
// 启用Knife4j增强Swagger功能
// 配置类的声明
// 启用Swagger2
// 导入BeanValidator插件配置
// 解决Swagger 3.X以下版本的兼容性问题，但会导致3.X及以上版本页面无法打开
//@EnableWebMvc
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    // 定义Swagger文档的标题
    private static final String SWAGGER_TITLE = "校园墙项目API接口文档";
    // 定义Swagger文档的版本
    private static final String VERSION = "3.0.3";

    /**
     * 创建RESTful API的Docket对象
     *
     * @return Docket对象，用于配置Swagger
     */
    @Bean
    public Docket createRestApi() {
        // 创建并配置Docket实例
        return new Docket(DocumentationType.OAS_30) // 使用OAS 3.0规范来创建API文档
                .enable(true) // 启用Swagger功能
                // .useDefaultResponseMessages(false) // 可选：禁用默认的响应消息
                .apiInfo(apiInfo()) // 设置API的基本信息，如标题、描述等
                .groupName("1.0 版本") // 设置API分组名称
                .select() // 开始选择哪些接口将被包含在文档中
                // 方式一: 配置扫描所有想在Swagger界面统一管理的接口，这些接口必须在此包下
//                .apis(RequestHandlerSelectors.basePackage("com.campus.wall.web.controller"))
                // 方式二: 只有当方法上有 @ApiOperation 注解时才能生成对应的接口文档
                .apis(RequestHandlerSelectors.withMethodAnnotation(ApiOperation.class))
                .paths(PathSelectors.regex("(?!/error.*).*"))
                .paths(PathSelectors.any()) // 包含所有路径
                .build() // 构建并返回配置好的Docket实例
                .ignoredParameterTypes(BasicErrorController.class);
    }

    /**
     * 构建API的基本信息
     * @return ApiInfo对象，包含API的元数据
     */
    private ApiInfo apiInfo() {
        // 使用ApiInfoBuilder构建API信息，用于在Swagger UI中展示和描述API
        return new ApiInfoBuilder()
                // 设置API标题
                .title(SwaggerConfig.SWAGGER_TITLE)
                // 设置API描述
                .description("# 校园墙项目API接口文档简介")
                // 设置API的服务条款URL
                .termsOfServiceUrl("http://127.0.0.1/#/login")
                // 设置API的联系人信息
                .contact(new Contact("MrJoe", "", "suvadawendi@gmail.com"))
                // 设置API版本
                .version(SwaggerConfig.VERSION)
                // 构建ApiInfo对象
                .build();
    }
}

