package com.campus.wall.service;

import com.campus.wall.dao.entity.Users;
import com.campus.wall.dao.repository.ResponseResult;

public interface LoginService {
    ResponseResult login(Users users);
}
