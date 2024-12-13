package com.campus.framework.filter;

import com.alibaba.fastjson.JSON;
import com.campus.framework.dao.entity.LoginUser;
import com.campus.framework.dao.enums.AppHttpCodeEnum;
import com.campus.framework.dao.repository.ResponseResult;
import com.campus.framework.untils.JwtUtil;
import com.campus.framework.untils.RedisCache;
import com.campus.framework.untils.WebUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
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
        String authHeader = request.getHeader("Authorization");
        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(7);
        Claims claims;
        try {
            claims = JwtUtil.parseJWT(token);
        } catch (ExpiredJwtException e) { // JWT Token 过期
            e.printStackTrace();
            ResponseResult result = ResponseResult.errorResult(AppHttpCodeEnum.TOKEN_EXPIRED);
            WebUtils.renderString(response, JSON.toJSONString(result));
            return;
        } catch (Exception e) { // 其他解析失败情况
            e.printStackTrace();
            ResponseResult result = ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
            WebUtils.renderString(response, JSON.toJSONString(result));
            return;
        }

        String userId = claims.getSubject();
        LoginUser loginUser = redisCache.getCacheObject("login:" + userId);

        if (Objects.isNull(loginUser)) { // Redis 中没有用户信息
            ResponseResult result = ResponseResult.errorResult(AppHttpCodeEnum.REDIS_EXPIRED);
            WebUtils.renderString(response, JSON.toJSONString(result));
            return;
        }

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
        filterChain.doFilter(request, response);
    }

}
