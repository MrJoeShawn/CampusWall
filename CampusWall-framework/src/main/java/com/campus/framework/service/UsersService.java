package com.campus.framework.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.framework.dao.entity.Users;
import com.campus.framework.dao.repository.ResponseResult;

public interface UsersService extends IService<Users> {
    ResponseResult updateUserInfo(Users users);

    ResponseResult getUserInfo();
}
