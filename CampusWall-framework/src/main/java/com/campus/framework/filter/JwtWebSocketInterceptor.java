package com.campus.framework.filter;

import com.campus.framework.dao.entity.LoginUser;
import com.campus.framework.untils.JwtUtil;
import com.campus.framework.untils.RedisCache;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;

@Component
public class JwtWebSocketInterceptor implements HandshakeInterceptor {

    @Autowired
    private RedisCache redisCache;  // 注入 Redis 缓存，获取已登录的用户信息

    /**
     * 在 WebSocket 握手之前进行 token 校验
     * @param request 当前的 HTTP 请求
     * @param response 当前的 HTTP 响应
     * @param wsHandler WebSocket 处理器
     * @param attributes 用于存储会话中的属性
     * @return true 表示继续握手，false 表示拒绝握手
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response,
                                   WebSocketHandler wsHandler, Map<String, Object> attributes) {
        // 确保请求是 Servlet 类型的请求
        if (!(request instanceof ServletServerHttpRequest)) {
            return false;  // 如果不是，就拒绝握手
        }

        // 获取 HTTP 请求中的 token 参数
        HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
        String token = servletRequest.getParameter("token"); // WebSocket 连接时携带 JWT token

        // 如果 token 为空，则拒绝连接
        if (!StringUtils.hasText(token)) {
            return false;
        }

        try {
            // 解析 JWT token 获取 Claims 对象
            Claims claims = JwtUtil.parseJWT(token);
            String userId = claims.getSubject();  // 获取 token 中的用户 ID

            // 从 Redis 缓存中获取登录用户信息
            LoginUser loginUser = redisCache.getCacheObject("login:" + userId);
            if (Objects.isNull(loginUser)) {
                return false;  // 如果没有找到用户信息，拒绝连接
            }

            // 将用户 ID 和用户信息存入 WebSocket 会话的属性中
            attributes.put("userId", Long.parseLong(userId));
            attributes.put("loginUser", loginUser);

            // 返回 true，表示继续握手，允许连接
            return true;
        } catch (Exception e) {
            e.printStackTrace();  // 打印异常信息
            return false;  // 如果解析 token 或获取用户信息失败，拒绝连接
        }
    }

    /**
     * WebSocket 握手后执行的操作，通常用于日志记录
     * @param request 当前的 HTTP 请求
     * @param response 当前的 HTTP 响应
     * @param wsHandler WebSocket 处理器
     * @param exception 握手过程中发生的异常
     */
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response,
                               WebSocketHandler wsHandler, Exception exception) {
        // 握手后没有额外的操作，方法为空
    }
}

