package com.campus.wall.web.controller;

import com.campus.wall.dao.entity.Users;
import com.campus.wall.service.UsersService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/user")
@Api(tags = "查询用户测试接口")
public class UsersController {

    @Autowired
    private UsersService usersService;

    @ApiOperation(value = "查询所有用户")
    @GetMapping("/getUser")
    public List<Users> getUser() {
        return usersService.list();
    }
}
