package com.campus.framework.filter;

import com.alibaba.fastjson.JSON;
import com.campus.framework.dao.entity.LoginUser;
import com.campus.framework.dao.enums.AppHttpCodeEnum;
import com.campus.framework.dao.repository.ResponseResult;
import com.campus.framework.untils.JwtUtil;
import com.campus.framework.untils.RedisCache;
import com.campus.framework.untils.WebUtils;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

/**
 * JWT 认证 Token 过滤器类
 * 该类继承自 OncePerRequestFilter，用于处理 JWT Token 的认证逻辑
 */
@Component
public class JwtAuthenticationTokenFilter extends OncePerRequestFilter {

    /**
     * Redis 缓存工具类，用于从 Redis 中获取用户登录信息
     */
    @Autowired
    private RedisCache redisCache;

    /**
     * 执行过滤器的主要逻辑
     *
     * @param request  HTTP 请求对象，用于获取请求头中的 JWT Token
     * @param response HTTP 响应对象，用于向客户端返回信息
     * @param filterChain 过滤器链，用于继续执行下一个过滤器或请求的处理
     * @throws ServletException 如果发生异常，会抛出此异常
     * @throws IOException 如果发生 IO 异常，会抛出此异常
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 获取请求头中的 Authorization
        String authHeader = request.getHeader("Authorization");
        // 如果 Authorization 为空或者不以 "Bearer " 开头，直接放行
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 去掉 "Bearer " 前缀获取 token
        String token = authHeader.substring(7);
//        System.out.println("Authorization Header: " + authHeader); // 调试输出
        Claims claims = null;
        try {
            claims = JwtUtil.parseJWT(token);
        } catch (Exception e) {
            e.printStackTrace();
            // 解析 Token 失败，返回需要登录的响应
            ResponseResult result = ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
            WebUtils.renderString(response, JSON.toJSONString(result));
            return;
        }

        // 获取 Token 中的用户 ID
        String userId = claims.getSubject();
        // 从 Redis 中获取登录用户信息
        LoginUser loginUser = redisCache.getCacheObject("login:" + userId);

        // 如果 Redis 中没有找到用户信息，说明 token 已经过期
        if (Objects.isNull(loginUser)) {
            // 返回需要登录的响应
            ResponseResult result = ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
            WebUtils.renderString(response, JSON.toJSONString(result));
            return;
        }

        // 设置 Authentication 对象，Spring Security 会从这里判断是否已认证
        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());

        // 将认证信息放入 Security 上下文中
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        // 继续执行请求的处理
        filterChain.doFilter(request, response);
    }
}
