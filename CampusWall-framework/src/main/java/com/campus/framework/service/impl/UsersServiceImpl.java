package com.campus.framework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

    /**
     * 更新用户信息
     * @param users
     * @return
     */
    @Override
    public ResponseResult updateUserInfo(Users users) {
//        System.out.println("updateUserInfo" + users);
        try {
            Integer userId = SecurityUtils.getUserId();
            Users updateUser = usersMapper.selectById(userId);
            if (updateUser == null) {
                log.warn("用户ID {} 不存在", userId);
                return ResponseResult.errorResult(AppHttpCodeEnum.USER_NOT_FOUND);
            }
            System.out.println("用户的出生日期: " + users.getBirthdate());

            // 设置其他用户信息
            updateUser.setGender(users.getGender());
            updateUser.setBirthdate(users.getBirthdate());
            updateUser.setFullName(users.getFullName());
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


    /**
     * 管理员获取所有用户信息
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public ResponseResult getAllUserInfo(Integer pageNum, Integer pageSize) {
        try {
            // 1. 使用 MyBatis-Plus 的分页对象
            IPage<Users> page = new Page<>(pageNum, pageSize);

            // 2. 执行分页查询
            IPage<Users> userPage = usersMapper.selectPage(page, null);

            // 3. 判断数据是否为空
            if (userPage.getRecords().isEmpty()) {
                log.warn("未找到任何用户信息");
                return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_FOUND, "未找到任何用户信息");
            }

            // 4. 返回分页数据
            return ResponseResult.okResult(userPage);
        } catch (Exception e) {
            log.error("查询所有用户信息失败", e);
            return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR, "查询用户信息失败");
        }
    }

    /**
     * 添加用户
     * @param user
     * @return
     */
    @Override
    public ResponseResult addUser(Users user) {
        try {
            // 验证用户信息是否已存在，例如检查用户名或邮箱是否重复
            QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("username", user.getUsername()).or().eq("email", user.getEmail());
            Users existingUser = usersMapper.selectOne(queryWrapper);

            if (existingUser != null) {
                return ResponseResult.errorResult(AppHttpCodeEnum.USER_ALREADY_EXISTS, "用户名或邮箱已存在");
            }

            // 设置默认值（例如账户启用状态）
            user.setEnabled(1);
            user.setAccountNonExpired(1);
            user.setCredentialsNonExpired(1);
            user.setAccountNonLocked(1);

            // 插入用户数据
            int result = usersMapper.insert(user);
            if (result > 0) {
                return ResponseResult.okResult("用户添加成功");
            } else {
                return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR, "添加用户失败");
            }
        } catch (Exception e) {
            log.error("添加用户失败", e);
            return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR, "添加用户失败");
        }
    }

    /**
     * 删除用户
     * @param id
     * @return
     */
    @Override
    public ResponseResult deleteUser(Integer id) {
        try {
            // 查找用户
            Users user = usersMapper.selectById(id);
            if (user == null) {
                return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_FOUND, "用户不存在");
            }

            // 删除用户
            int result = usersMapper.deleteById(id);
            if (result > 0) {
                return ResponseResult.okResult("用户删除成功");
            } else {
                return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR, "删除用户失败");
            }
        } catch (Exception e) {
            log.error("删除用户失败", e);
            return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR, "删除用户失败");
        }
    }

    /**
     * 更新用户信息
     * @param user
     * @return
     */
    @Override
    public ResponseResult updateUser(Users user) {
        try {
            // 查找用户
            Users existingUser = usersMapper.selectById(user.getId());
            if (existingUser == null) {
                return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_FOUND, "用户不存在");
            }

            // 更新用户信息
            int result = usersMapper.updateById(user);
            if (result > 0) {
                return ResponseResult.okResult("用户信息更新成功");
            } else {
                return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR, "更新用户信息失败");
            }
        } catch (Exception e) {
            log.error("更新用户信息失败", e);
            return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR, "更新用户信息失败");
        }
    }

    /**
     * 根据用户ID获取用户信息
     * @param id 用户ID
     * @return 用户信息
     */
    @Override
    public ResponseResult getUserById(Integer id) {
        try {
            // 查询用户信息
            Users user = usersMapper.selectById(id);
            if (user == null) {
                log.warn("用户ID {} 不存在", id);
                return ResponseResult.errorResult(AppHttpCodeEnum.USER_NOT_FOUND, "用户信息不存在");
            }

            // 返回用户信息
            return ResponseResult.okResult(user);
        } catch (Exception e) {
            log.error("查询用户信息失败", e);
            return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR, "查询用户信息失败");
        }
    }


    /**
     * 获取用户信息
     * @return
     */
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
            System.out.println("getUserInfo" + userInfoVo);
            return ResponseResult.okResult(userInfoVo);
        } catch (Exception e) {
            log.error("获取用户信息失败", e);
            return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR, "获取用户信息失败");
        }
    }
}
