package com.campus.framework.config;

import com.campus.framework.filter.JwtWebSocketInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket  // 启用 WebSocket 配置
public class WebSocketConfig implements WebSocketConfigurer {

    @Autowired
    private JwtWebSocketInterceptor jwtWebSocketInterceptor;  // 注入 JWT WebSocket 拦截器，用于验证 WebSocket 请求中的 token

    @Autowired
    private ChatWebSocketHandler chatWebSocketHandler;  // 注入自定义 WebSocket 处理器，用于处理消息收发

    /**
     * 注册 WebSocket 处理器和拦截器
     * @param registry WebSocketHandlerRegistry，WebSocket 注册器
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        // 注册 WebSocket 处理器，并配置拦截器、跨域等设置
        registry.addHandler(chatWebSocketHandler, "/ws/chat")  // 设置 WebSocket 的 URL 路径
                .addInterceptors(jwtWebSocketInterceptor)  // 添加 JWT 拦截器，进行 token 验证
                .setAllowedOrigins("*");  // 允许所有来源进行跨域访问
    }
}


