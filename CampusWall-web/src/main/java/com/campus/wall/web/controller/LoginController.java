package com.campus.wall.web.controller;

import com.campus.framework.dao.entity.Users;
import com.campus.framework.dao.enums.AppHttpCodeEnum;
import com.campus.framework.dao.repository.ResponseResult;
import com.campus.framework.service.LoginService;
import com.campus.framework.web.exception.SystemException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class LoginController {

    @Autowired
    private LoginService loginService;

    /**
     * 登录接口
     */
    @PostMapping("/login")
    public ResponseResult login (@RequestBody Users users){
        if(!StringUtils.hasText(users.getUsername())){
            throw new SystemException(AppHttpCodeEnum.REQUIRE_USERNAME);
        }
        return loginService.login(users);
    }

    /**
     * 退出登录接口
     */
    @PostMapping("/logout")
    public ResponseResult logout(){
        return loginService.logout();
    }


    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ResponseResult register(@RequestBody Users users) {
        return loginService.register(users);
    }
}
