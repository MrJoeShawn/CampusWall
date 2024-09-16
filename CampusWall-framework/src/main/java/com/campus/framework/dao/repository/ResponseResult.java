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
    private static final long serialVersionUID = 1L;

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
        this(AppHttpCodeEnum.SUCCESS.getCode(), AppHttpCodeEnum.SUCCESS.getMsg(), null);
    }

    /**
     * 带状态码和数据的构造函数
     *
     * @param code 响应状态码
     * @param data 响应数据
     */
    public ResponseResult(Integer code, T data) {
        this(code, null, data);
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
     * 构建错误结果的静态方法
     *
     * @param code 状态码
     * @param msg  消息
     * @return 构建的错误结果对象
     */
    public static <T> ResponseResult<T> errorResult(int code, String msg) {
        return new ResponseResult<>(code, msg, null);
    }

    /**
     * 构建成功结果的静态方法
     *
     * @return 构建的成功结果对象
     */
    public static <T> ResponseResult<T> okResult() {
        return new ResponseResult<>();
    }

    /**
     * 构建带消息的成功结果的静态方法
     *
     * @param msg 消息
     * @return 构建的成功结果对象
     */
    public static <T> ResponseResult<T> okResult(String msg) {
        return new ResponseResult<>(AppHttpCodeEnum.SUCCESS.getCode(), msg, null);
    }

    /**
     * 构建带数据的成功结果的静态方法
     *
     * @param data 数据
     * @return 构建的成功结果对象
     */
    public static <T> ResponseResult<T> okResult(T data) {
        return new ResponseResult<>(AppHttpCodeEnum.SUCCESS.getCode(), AppHttpCodeEnum.SUCCESS.getMsg(), data);
    }


    public static <T> ResponseResult<T> okResult(T data, String msg) {
        return new ResponseResult<>(AppHttpCodeEnum.SUCCESS.getCode(), msg, data);
    }


    /**
     * 构建错误结果的静态方法，使用枚举类型定义错误信息
     *
     * @param enums 枚举类型错误信息
     * @return 构建的错误结果对象
     */
    public static <T> ResponseResult<T> errorResult(AppHttpCodeEnum enums) {
        return errorResult(enums.getCode(), enums.getMsg());
    }

    /**
     * 构建带自定义消息的错误结果的静态方法，使用枚举类型和自定义消息
     *
     * @param enums 枚举类型错误信息
     * @param msg   自定义错误消息
     * @return 构建的错误结果对象
     */
    public static <T> ResponseResult<T> errorResult(AppHttpCodeEnum enums, String msg) {
        return errorResult(enums.getCode(), msg);
    }

    // Getter 和 Setter 方法

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
