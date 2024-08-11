package com.campus.framework.dao.enums;

/**
 * 应用HTTP状态码枚举
 * 用于表示不同HTTP状态码及其描述
 */
public enum AppHttpCodeEnum {
    // 成功
    SUCCESS(200,"操作成功"),
    // 登录
    NEED_LOGIN(401,"需要登录后操作"),
    // 权限
    NO_OPERATOR_AUTH(403,"无权限操作"),
    // 服务器错误
    SYSTEM_ERROR(500,"出现错误"),
    // 用户名已存在
    USERNAME_EXIST(501,"用户名已存在"),
    // 手机号已存在
    PHONENUMBER_EXIST(502,"手机号已存在"),
    // 邮箱已存在
    EMAIL_EXIST(503, "邮箱已存在"),
    // 需要用户名
    REQUIRE_USERNAME(504, "必需填写用户名"),
    // 登录错误
    LOGIN_ERROR(505,"用户名或密码错误");

    // 状态码
    int code;
    // 错误消息
    String msg;

    /**
     * 构造函数，初始化状态码和错误消息
     * @param code 状态码
     * @param errorMessage 错误消息描述
     */
    AppHttpCodeEnum(int code, String errorMessage){
        this.code = code;
        this.msg = errorMessage;
    }

    /**
     * 获取状态码
     * @return 状态码
     */
    public int getCode() {
        return code;
    }

    /**
     * 获取错误消息
     * @return 错误消息
     */
    public String getMsg() {
        return msg;
    }
}

