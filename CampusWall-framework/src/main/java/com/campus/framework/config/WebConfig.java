package com.campus.framework.config;

import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.alibaba.fastjson.support.config.FastJsonConfig;
import com.alibaba.fastjson.support.spring.FastJsonHttpMessageConverter;
import com.alibaba.fastjson.serializer.ToStringSerializer; // 修改这里，使用 FastJSON 的 ToStringSerializer
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // 设置允许跨域的路径
        registry.addMapping("/**")
                // 设置允许跨域请求的域名
                .allowedOriginPatterns("*")
                // 是否允许cookie
                .allowCredentials(true)
                // 设置允许的请求方式
                .allowedMethods("GET", "POST", "DELETE", "PUT", "PATCH")
                // 设置允许的header属性
                .allowedHeaders("*")
                // 跨域允许时间
                .maxAge(3600);
    }

    /**
     * 使用@Bean注入fastJsonHttpMessageConverter
     *
     * @return 返回HttpMessageConverter类型的fastJsonHttpMessageConverters对象
     */
    @Bean
    public HttpMessageConverter<?> fastJsonHttpMessageConverters() {
        // 1. 创建一个FastJsonHttpMessageConverter对象，用于转换消息
        FastJsonHttpMessageConverter fastConverter = new FastJsonHttpMessageConverter();

        // 2. 创建一个FastJsonConfig对象，用于配置FastJson的相关属性
        FastJsonConfig fastJsonConfig = new FastJsonConfig();
        // 设置序列化特性，输出格式化
        fastJsonConfig.setSerializerFeatures(SerializerFeature.PrettyFormat);
        // 设置日期格式
        fastJsonConfig.setDateFormat("yyyy-MM-dd HH:mm:ss");

        // 3. 设置全局的序列化配置，将Long类型转换为字符串类型
        SerializeConfig serializeConfig = new SerializeConfig();
        serializeConfig.put(Long.class, ToStringSerializer.instance); // 使用 FastJSON 的 ToStringSerializer
        fastJsonConfig.setSerializeConfig(serializeConfig);

        // 4. 将FastJsonConfig对象设置到FastJsonHttpMessageConverter中
        fastConverter.setFastJsonConfig(fastJsonConfig);

        // 5. 返回HttpMessageConverter类型的对象
        return fastConverter;
    }

    /**
     * 重写configureMessageConverters方法，添加fastjson转换器
     *
     * @param converters 转换器列表
     * @return void
     */
    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        converters.add(fastJsonHttpMessageConverters());
    }
}
