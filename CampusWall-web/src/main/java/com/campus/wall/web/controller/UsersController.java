package com.campus.wall.web.controller;

import com.campus.framework.dao.entity.Users;
import com.campus.framework.dao.repository.ResponseResult;
import com.campus.framework.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class UsersController {

    @Autowired
    private UsersService usersService;

    @GetMapping("/userInfo")
    public ResponseResult getUserInfo(){
        return usersService.getUserInfo();
    }


    @PutMapping("/updateUser")
    public ResponseResult updateUserInfo(@RequestBody Users users) {
        return usersService.updateUserInfo(users);
    }

}
