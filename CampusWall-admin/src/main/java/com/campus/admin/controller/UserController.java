package com.campus.admin.controller;


import com.campus.framework.dao.entity.Users;
import com.campus.framework.dao.repository.ResponseResult;
import com.campus.framework.service.UsersService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class UserController {

    @Autowired
    private UsersService usersService;

    /**
     * 获取用户信息
     * @return
     */
    @GetMapping("/userAllInfo")
    public ResponseResult getAllUserInfo(Integer pageNum,Integer pageSize){
        return usersService.getAllUserInfo(pageNum,pageSize);
    }

    /**
     * 添加用户
     * @return
     */
    @PostMapping("/addUser")
    public ResponseResult addUser(@RequestBody Users user) {
        return usersService.addUser(user);
    }

    /**
     * 删除用户
     * @return
     */
    @DeleteMapping("/deleteUser/{id}")
    public ResponseResult deleteUser(@PathVariable Integer id) {
        return usersService.deleteUser(id);
    }

    /**
     * 修改用户信息
     * @return
     */
    @PutMapping("/updateUser")
    public ResponseResult updateUser(@RequestBody Users user) {
        return usersService.updateUser(user);
    }
}
