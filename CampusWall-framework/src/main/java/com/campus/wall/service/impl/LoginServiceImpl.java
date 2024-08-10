package com.campus.wall.service.impl;

import com.campus.wall.dao.entity.LoginUser;
import com.campus.wall.dao.entity.Users;
import com.campus.wall.dao.repository.ResponseResult;
import com.campus.wall.dao.vo.UserInfoVo;
import com.campus.wall.dao.vo.UserLoginVo;
import com.campus.wall.service.LoginService;
import com.campus.wall.untils.BeanCopyUtils;
import com.campus.wall.untils.JwtUtil;
import com.campus.wall.untils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
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
        UsernamePasswordAuthenticationToken authenticationToken = new
                UsernamePasswordAuthenticationToken(users.getUsername(),users.getPassword());
        Authentication authenticate =
                authenticationManager.authenticate(authenticationToken);
        //判断是否认证通过
        if(Objects.isNull(authenticate)){
            throw new RuntimeException("用户名或密码错误");
        }
        //获取userid 生成token
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        String userId = loginUser.getUsers().getId().toString();
        String jwt = JwtUtil.createJWT(userId);
        //把用户信息存入redis
        redisCache.setCacheObject("login:"+userId,loginUser);
        //把token和userinfo封装 返回
        //把User转换成UserInfoVo
        UserInfoVo userInfoVo = BeanCopyUtils.copyBean(loginUser.getUsers(),
                UserInfoVo.class);
        UserLoginVo vo = new UserLoginVo(jwt,userInfoVo);
        return ResponseResult.okResult(vo);
    }
}
