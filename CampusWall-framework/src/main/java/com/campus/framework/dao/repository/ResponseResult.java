package com.campus.framework.dao.repository;

import com.campus.framework.dao.enums.AppHttpCodeEnum;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;
/**
 * 响应结果泛型类，用于统一API接口返回格式
 * 实现了Serializable接口，表示该类对象可以被序列化
 * 通过@JsonInclude注解指定只序列化非null的字段
 *
 * @param <T> 泛型参数，表示数据类型
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ResponseResult<T> implements Serializable {
    // 响应状态码
    private Integer code;
    // 响应消息
    private String msg;
    // 响应数据
    private T data;

    /**
     * 默认构造函数，初始化为成功状态
     */
    public ResponseResult() {
        this.code = AppHttpCodeEnum.SUCCESS.getCode();
        this.msg = AppHttpCodeEnum.SUCCESS.getMsg();
    }

    /**
     * 带状态码和数据的构造函数
     *
     * @param code 响应状态码
     * @param data 响应数据
     */
    public ResponseResult(Integer code, T data) {
        this.code = code;
        this.data = data;
    }

    /**
     * 带状态码、消息和数据的构造函数
     *
     * @param code 响应状态码
     * @param msg  响应消息
     * @param data 响应数据
     */
    public ResponseResult(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 带状态码和消息的构造函数
     *
     * @param code 响应状态码
     * @param msg  响应消息
     */
    public ResponseResult(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 构建错误结果的静态方法
     *
     * @param code 状态码
     * @param msg  消息
     * @return 构建的错误结果对象
     */
    public static ResponseResult errorResult(int code, String msg) {
        ResponseResult result = new ResponseResult();
        return result.error(code, msg);
    }

    /**
     * 构建成功结果的静态方法
     *
     * @return 构建的成功结果对象
     */
    public static ResponseResult okResult() {
        ResponseResult result = new ResponseResult();
        return result;
    }

    /**
     * 构建带消息的成功结果的静态方法
     *
     * @param code 状态码
     * @param msg  消息
     * @return 构建的成功结果对象
     */
    public static ResponseResult okResult(int code, String msg) {
        ResponseResult result = new ResponseResult();
        return result.ok(code, null, msg);
    }

    /**
     * 构建带数据的成功结果的静态方法
     *
     * @param data 数据
     * @return 构建的成功结果对象
     */
    public static ResponseResult okResult(Object data) {
        ResponseResult result = setAppHttpCodeEnum(AppHttpCodeEnum.SUCCESS,
                AppHttpCodeEnum.SUCCESS.getMsg());
        if (data != null) {
            result.setData(data);
        }
        return result;
    }

    /**
     * 构建错误结果的静态方法，使用枚举类型定义错误信息
     *
     * @param enums 枚举类型错误信息
     * @return 构建的错误结果对象
     */
    public static ResponseResult errorResult(AppHttpCodeEnum enums) {
        return setAppHttpCodeEnum(enums, enums.getMsg());
    }

    /**
     * 构建带自定义消息的错误结果的静态方法，使用枚举类型和自定义消息
     *
     * @param enums 枚举类型错误信息
     * @param msg   自定义错误消息
     * @return 构建的错误结果对象
     */
    public static ResponseResult errorResult(AppHttpCodeEnum enums, String msg) {
        return setAppHttpCodeEnum(enums, msg);
    }

    /**
     * 设置AppHttpCodeEnum的静态辅助方法，用于统一设置响应状态码和消息
     *
     * @param enums 枚举类型错误信息
     * @return 构建的响应结果对象
     */
    public static ResponseResult setAppHttpCodeEnum(AppHttpCodeEnum enums) {
        return okResult(enums.getCode(), enums.getMsg());
    }

    /**
     * 设置AppHttpCodeEnum和自定义消息的静态辅助方法
     *
     * @param enums 枚举类型错误信息
     * @param msg   自定义消息
     * @return 构建的响应结果对象
     */
    private static ResponseResult setAppHttpCodeEnum(AppHttpCodeEnum enums,
                                                     String msg) {
        return okResult(enums.getCode(), msg);
    }

    /**
     * 设置错误状态的辅助方法
     *
     * @param code 状态码
     * @param msg  消息
     * @return 当前对象，支持链式调用
     */
    public ResponseResult<?> error(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
        return this;
    }

    /**
     * 设置成功状态和数据的辅助方法
     *
     * @param code 状态码
     * @param data 响应数据
     * @return 当前对象，支持链式调用
     */
    public ResponseResult<?> ok(Integer code, T data) {
        this.code = code;
        this.data = data;
        return this;
    }

    /**
     * 设置成功状态、数据和消息的辅助方法
     *
     * @param code 状态码
     * @param data 响应数据
     * @param msg  消息
     * @return 当前对象，支持链式调用
     */
    public ResponseResult<?> ok(Integer code, T data, String msg) {
        this.code = code;
        this.data = data;
        this.msg = msg;
        return this;
    }

    /**
     * 设置成功状态和数据的辅助方法，使用默认成功状态码
     *
     * @param data 响应数据
     * @return 当前对象，支持链式调用
     */
    public ResponseResult<?> ok(T data) {
        this.data = data;
        return this;
    }

    /**
     * 获取响应状态码
     *
     * @return 响应状态码
     */
    public Integer getCode() {
        return code;
    }

    /**
     * 设置响应状态码
     *
     * @param code 响应状态码
     */
    public void setCode(Integer code) {
        this.code = code;
    }

    /**
     * 获取响应消息
     *
     * @return 响应消息
     */
    public String getMsg() {
        return msg;
    }

    /**
     * 设置响应消息
     *
     * @param msg 响应消息
     */
    public void setMsg(String msg) {
        this.msg = msg;
    }

    /**
     * 获取响应数据
     *
     * @return 响应数据
     */
    public T getData() {
        return data;
    }

    /**
     * 设置响应数据
     *
     * @param data 响应数据
     */
    public void setData(T data) {
        this.data = data;
    }
}
