package com.campus.framework.service;

import com.campus.framework.dao.entity.Users;
import com.campus.framework.dao.repository.ResponseResult;

public interface LoginService {
    ResponseResult logout();

    ResponseResult login(Users users);

    ResponseResult register(Users users);
}
