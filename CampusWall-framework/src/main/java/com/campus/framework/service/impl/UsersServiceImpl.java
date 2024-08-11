package com.campus.framework.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.framework.dao.mapper.UsersMapper;
import com.campus.framework.dao.entity.Users;
import com.campus.framework.service.UsersService;
import org.springframework.stereotype.Service;

@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements UsersService {
}
