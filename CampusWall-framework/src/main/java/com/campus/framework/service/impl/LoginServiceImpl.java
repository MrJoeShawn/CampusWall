package com.campus.framework.service.impl;

import com.campus.framework.dao.entity.LoginUser;
import com.campus.framework.dao.entity.Users;
import com.campus.framework.dao.repository.ResponseResult;
import com.campus.framework.dao.vo.UserInfoVo;
import com.campus.framework.dao.vo.UserLoginVo;
import com.campus.framework.service.LoginService;
import com.campus.framework.untils.BeanCopyUtils;
import com.campus.framework.untils.JwtUtil;
import com.campus.framework.untils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisCache redisCache;


    @Override
    public ResponseResult login(Users users) {
        // 创建包含用户名和密码的认证令牌
        UsernamePasswordAuthenticationToken authenticationToken = new
                UsernamePasswordAuthenticationToken(users.getUsername(),users.getPassword());
        // 使用认证管理器进行用户认证
        Authentication authenticate =
                authenticationManager.authenticate(authenticationToken);
        //判断是否认证通过
        if(Objects.isNull(authenticate)){
            throw new RuntimeException("用户名或密码错误");
        }
        // 获取用户ID并生成会话令牌
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        String userId = loginUser.getUsers().getId().toString();
        String jwt = JwtUtil.createJWT(userId);
        // 把用户信息存入Redis缓存
        redisCache.setCacheObject("login:"+userId,loginUser);
        // 把令牌和用户信息封装并返回
        UserInfoVo userInfoVo = BeanCopyUtils.copyBean(loginUser.getUsers(),
                UserInfoVo.class);
        UserLoginVo vo = new UserLoginVo(jwt,userInfoVo);
        return ResponseResult.okResult(vo);
    }

    @Override
    public ResponseResult logout() {
        // 从Security上下文中获取当前认证信息
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        // 获取当前登录用户信息
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        // 获取用户ID
        Long userId = Long.valueOf(loginUser.getUsers().getId());
        // 删除Redis中存储的当前用户信息
        redisCache.deleteObject("login:"+userId);
        return ResponseResult.okResult();
    }
}
