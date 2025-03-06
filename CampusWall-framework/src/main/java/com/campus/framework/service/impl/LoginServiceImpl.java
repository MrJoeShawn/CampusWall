package com.campus.framework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.campus.framework.dao.entity.LoginUser;
import com.campus.framework.dao.entity.Users;
import com.campus.framework.dao.enums.AppHttpCodeEnum;
import com.campus.framework.dao.mapper.UsersMapper;
import com.campus.framework.dao.repository.ResponseResult;
import com.campus.framework.dao.vo.UserInfoVo;
import com.campus.framework.dao.vo.UserLoginVo;
import com.campus.framework.service.LoginService;
import com.campus.framework.untils.BeanCopyUtils;
import com.campus.framework.untils.JwtUtil;
import com.campus.framework.untils.RedisCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.sql.Timestamp;
import java.util.Objects;

@Service
public class LoginServiceImpl implements LoginService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private UsersMapper usersMapper;  // 用户数据库操作类

    @Autowired
    private PasswordEncoder passwordEncoder; // Spring Security 提供的密码加密工具


    @Override
    public ResponseResult login(Users users) {
        // 创建包含用户名和密码的认证令牌
        UsernamePasswordAuthenticationToken authenticationToken = new
                UsernamePasswordAuthenticationToken(users.getUsername(),users.getPassword());
        // 使用认证管理器进行用户认证
        Authentication authenticate =
                authenticationManager.authenticate(authenticationToken);
        //判断是否认证通过
        if(Objects.isNull(authenticate)){
            throw new RuntimeException("用户名或密码错误");
        }
        // 获取用户ID并生成会话令牌
        LoginUser loginUser = (LoginUser) authenticate.getPrincipal();
        String userId = loginUser.getUsers().getId().toString();
        String jwt = JwtUtil.createJWT(userId);
        // 把用户信息存入Redis缓存
        redisCache.setCacheObject("login:"+userId,loginUser);
        // 把令牌和用户信息封装并返回
        UserInfoVo userInfoVo = BeanCopyUtils.copyBean(loginUser.getUsers(),
                UserInfoVo.class);
        UserLoginVo vo = new UserLoginVo(jwt,userInfoVo);
        return ResponseResult.okResult(vo);
    }


    @Override
    public ResponseResult register(Users users) {
        System.out.println("Received user: " + users);

        // 1. 参数校验
        if (!StringUtils.hasText(users.getUsername()) || !StringUtils.hasText(users.getPassword()) || !StringUtils.hasText(users.getEmail())) {
            return ResponseResult.errorResult(400, "用户名、密码和邮箱不能为空");
        }

        // 2. 检查用户名和邮箱是否已存在
        LambdaQueryWrapper<Users> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Users::getUsername, users.getUsername()).or().eq(Users::getEmail, users.getEmail());
        Users existUser = usersMapper.selectOne(queryWrapper);
        if (existUser != null) {
            return ResponseResult.errorResult(400, "用户名或邮箱已存在");
        }

        // todo 未对密码进行加密
        // 3. 对密码进行加密
        users.setPassword(passwordEncoder.encode(users.getPassword()));

        // 4. 设置默认值（如账户启用、邮箱未验证）
        users.setEnabled(1);
        users.setAccountNonExpired(1);
        users.setCredentialsNonExpired(1);
        users.setAccountNonLocked(1);
        users.setEmailVerified(1);
        users.setCreatedAt(new Timestamp(System.currentTimeMillis()));

        // 5. 保存到数据库
        int insertResult = usersMapper.insert(users);
        if (insertResult <= 0) {
            return ResponseResult.errorResult(500, "注册失败，请重试");
        }

        // 6. 返回成功信息
        return ResponseResult.okResult("注册成功");
    }

    /**
     * 退出登录
     * @return
     */
    @Override
    public ResponseResult logout() {
        // 从Security上下文中获取当前认证信息
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        // 获取当前登录用户信息
        LoginUser loginUser = (LoginUser) authentication.getPrincipal();
        // 获取用户ID
        Long userId = Long.valueOf(loginUser.getUsers().getId());
        // 删除Redis中存储的当前用户信息
        redisCache.deleteObject("login:"+userId);
        return ResponseResult.okResult();
    }
}
