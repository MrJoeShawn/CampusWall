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

@Service
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users> implements UsersService {
    private final UsersMapper usersMapper;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private HttpServletRequest request;

    public UsersServiceImpl(UsersMapper usersMapper) {
        this.usersMapper = usersMapper;
    }

    @Override
    public ResponseResult updateUserInfo(Users users) {
        //TODO 使用 DTO（数据传输对象）隔离可修改字段
            return null;

        }

    @Override
    public ResponseResult getUserInfo() {
        Integer userId = SecurityUtils.getUserId();
        Users user = usersMapper.selectById(userId);

        if (user == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR, "用户不存在");
        }

        // 转换为 VO 对象，返回用户信息
        UserInfoVo userInfoVo = BeanCopyUtils.copyBean(user, UserInfoVo.class);
        return ResponseResult.okResult(userInfoVo);
    }
}
