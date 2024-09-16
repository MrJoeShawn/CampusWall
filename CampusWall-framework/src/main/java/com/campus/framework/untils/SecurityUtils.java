package com.campus.framework.untils;


import com.campus.framework.dao.entity.LoginUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 安全工具类，提供关于当前登录用户的信息
 */
public class SecurityUtils
{
    /**
     * 获取当前登录的用户信息
     *
     * @return 当前登录的用户信息
     */
    public static LoginUser getLoginUser()
    {
        return (LoginUser) getAuthentication().getPrincipal();
    }

    /**
     * 获取当前的认证信息
     *
     * @return 当前的认证信息
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 判断当前用户是否为管理员
     *
     * @return true如果是管理员，否则false
     */
    public static Boolean isAdmin(){
        Integer id = getLoginUser().getUsers().getId();
        return id != null && 1L == id;
    }

    /**
     * 获取当前登录用户的ID
     *
     * @return 当前登录用户的ID
     */
    public static Integer getUserId() {
        return getLoginUser().getUsers().getId();
    }
}
