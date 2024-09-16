package com.campus.framework.web.exception;

import com.campus.framework.dao.enums.AppHttpCodeEnum;

/**
 * 自定义系统异常类
 * 继承自RuntimeException，用于处理系统异常
 * 该异常类增加了错误代码和自定义错误消息
 */
public class SystemException extends RuntimeException{
    // 错误代码
    private int code;
    // 错误消息
    private String msg;

    /**
     * 获取错误代码
     * @return 错误代码
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

    /**
     * 构造函数，通过AppHttpCodeEnum初始化SystemException
     * @param httpCodeEnum 包含错误代码和消息的枚举
     */
    public SystemException(AppHttpCodeEnum httpCodeEnum) {
        super(httpCodeEnum.getMsg()); // 调用父类的构造函数，设置异常消息
        this.code = httpCodeEnum.getCode(); // 设置错误代码
        this.msg = httpCodeEnum.getMsg(); // 设置错误消息
    }
}
