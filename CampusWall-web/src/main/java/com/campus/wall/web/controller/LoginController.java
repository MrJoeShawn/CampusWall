package com.campus.wall.web.controller;

import com.campus.wall.dao.entity.Users;
import com.campus.wall.dao.repository.ResponseResult;
import com.campus.wall.service.LoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(tags = "登录接口")
public class LoginController {

    @Autowired
    private LoginService loginService;

    @ApiOperation(value = "登录")
    @PostMapping("/login")
    public ResponseResult login (@RequestBody Users users){
        return loginService.login(users);
    }
}
