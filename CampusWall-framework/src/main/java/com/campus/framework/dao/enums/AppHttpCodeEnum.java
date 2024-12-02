package com.campus.framework.dao.enums;

/**
 * 应用HTTP状态码枚举
 * 用于表示不同HTTP状态码及其描述
 */
public enum AppHttpCodeEnum {
    // 成功
    SUCCESS(200, "操作成功"),
    // 登录
    NEED_LOGIN(401, "需要登录后操作"),
    // 权限
    NO_OPERATOR_AUTH(403, "无权限操作"),
    // 服务器错误
    SYSTEM_ERROR(500, "出现错误"),
    // 用户名已存在
    USERNAME_EXIST(601, "用户名已存在"),
    // 手机号已存在
    PHONENUMBER_EXIST(602, "手机号已存在"),
    // 邮箱已存在
    EMAIL_EXIST(603, "邮箱已存在"),
    // 需要用户名
    REQUIRE_USERNAME(604, "必需填写用户名"),
    // 文件类型错误
    FILE_TYPE_ERROR(605, "文件类型错误,请上传png或者jpg文件"),
    // 登录错误
    LOGIN_ERROR(606, "用户名或密码错误"),
    // 图片格式无效
    INVALID_IMAGE_FORMAT(607, "无效的图片格式，请上传PNG或JPG文件"),
    // 文件上传失败
    UPLOAD_FAILED(608, "文件上传失败"),
    // 用户不存在
    USER_NOT_FOUND(610, "用户不存在"),
    // 用户头像更新失败
    UPDATE_FAILED(609, "用户头像更新失败"),
    // 无效的用户ID
    INVALID_USER_ID(611, "无效的用户ID"),
    // 动态不存在
    DYNAMIC_NOT_FOUND(612, "动态不存在"),
    // 搜索内容不能为空
    CONTENT_NOT_EMPTY(613, "搜索内容不能为空"),
    // 查询动态时发生错误
    QUERY_DYNAMIC_ERROR(614, "查询动态时发生错误"),
    // 动态已经删除
    DYNAMIC_DELETED(615, "动态已经删除"),
    // 已设置为仅自己可见
    DYNAMIC_PRIVATE(616, "仅自己可见"),
    // 公开可见
    DYNAMIC_PUBLIC(617, "公开可见"),
    // 动态更新失败
    DYNAMIC_UPDATE_FAILED(618, "动态更新失败"),

    ;


    // 状态码
    private final int code;
    // 错误消息
    private final String msg;

    /**
     * 构造函数，初始化状态码和错误消息
     * @param code 状态码
     * @param msg 错误消息描述
     */
    AppHttpCodeEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
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
