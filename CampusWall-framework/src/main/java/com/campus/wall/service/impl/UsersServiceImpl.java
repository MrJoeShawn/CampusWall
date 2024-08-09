package com.campus.wall.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.wall.dao.entity.Users;
import com.campus.wall.dao.mapper.UsersMapper;
import com.campus.wall.service.UsersService;
import org.springframework.stereotype.Service;

@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements UsersService {
}
