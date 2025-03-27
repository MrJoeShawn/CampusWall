package com.campus.framework.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.framework.dao.entity.Users;
import com.campus.framework.dao.repository.ResponseResult;

/**
 * 用户表(Users)表服务接口
 *
 * @author makejava
 * @since 2024-11-10 16:12:55
 */
public interface UsersService extends IService<Users> {

    ResponseResult getUserInfo();

    ResponseResult updateUserInfo(Users users);

    ResponseResult getAllUserInfo(Integer pageNum, Integer pageSize);

    ResponseResult addUser(Users user);

    ResponseResult deleteUser(Integer id);

    ResponseResult updateUser(Users user);

    ResponseResult getUserById(Integer id);
}

