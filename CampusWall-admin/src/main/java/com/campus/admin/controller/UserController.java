package com.campus.admin.controller;


import com.campus.framework.dao.repository.ResponseResult;
import com.campus.framework.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin")
public class UserController {

    @Autowired
    private UsersService usersService;

    /**
     * 获取用户信息 用户点击个人主页时调用
     * @return
     */
    @GetMapping("/userAllInfo")
    public ResponseResult getAllUserInfo(Integer pageNum,Integer pageSize){
        return usersService.getAllUserInfo(pageNum,pageSize);
    }

}
