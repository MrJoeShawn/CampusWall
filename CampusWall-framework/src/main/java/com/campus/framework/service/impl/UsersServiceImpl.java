package com.campus.framework.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.framework.dao.enums.AppHttpCodeEnum;
import com.campus.framework.dao.mapper.UsersMapper;
import com.campus.framework.dao.entity.Users;
import com.campus.framework.dao.repository.ResponseResult;
import com.campus.framework.dao.vo.UserInfoVo;
import com.campus.framework.service.UsersService;
import com.campus.framework.untils.BeanCopyUtils;
import com.campus.framework.untils.RedisCache;
import com.campus.framework.untils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements UsersService {
    private final UsersMapper usersMapper;
    private final RedisCache redisCache;
    private final HttpServletRequest request;

    private static final Logger log = LoggerFactory.getLogger(UsersServiceImpl.class);

    @Autowired
    public UsersServiceImpl(UsersMapper usersMapper, RedisCache redisCache, HttpServletRequest request) {
        this.usersMapper = usersMapper;
        this.redisCache = redisCache;
        this.request = request;
    }

@Override
public ResponseResult updateUserInfo(Users users) {
    try {
        System.out.println(users);
        Integer userId = SecurityUtils.getUserId();
        Users updateUser = usersMapper.selectById(userId);
        if (updateUser == null) {
            log.warn("用户ID {} 不存在", userId);
            return ResponseResult.errorResult(AppHttpCodeEnum.USER_NOT_FOUND);
        }


        // 直接使用updateUser对象来设置新的信息
        updateUser.setFullName(users.getFullName());
        updateUser.setGender(users.getGender());
        updateUser.setBirthdate(users.getBirthdate());
        updateUser.setAddress(users.getAddress());
        updateUser.setMajor(users.getMajor());
        updateUser.setEmail(users.getEmail());
        updateUser.setPhoneNumber(users.getPhoneNumber());
        updateUser.setSchool(users.getSchool());

        // 更新数据库中的用户信息
        usersMapper.updateById(updateUser);

        return ResponseResult.okResult();
    } catch (Exception e) {
        log.error("更新用户信息失败", e);
        return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR, "更新用户信息失败");
    }
}


    @Override
    public ResponseResult getUserInfo() {
        try {
            Integer userId = SecurityUtils.getUserId();
            Users user = usersMapper.selectById(userId);

            if (user == null) {
                log.warn("用户ID {} 不存在", userId);
                return ResponseResult.errorResult(AppHttpCodeEnum.USER_NOT_FOUND);
            }

            // 转换为 VO 对象，返回用户信息
            UserInfoVo userInfoVo = BeanCopyUtils.copyBean(user, UserInfoVo.class);
            return ResponseResult.okResult(userInfoVo);
        } catch (Exception e) {
            log.error("获取用户信息失败", e);
            return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR, "获取用户信息失败");
        }
    }
}
