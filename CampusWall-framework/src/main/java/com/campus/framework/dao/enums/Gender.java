package com.campus.framework.dao.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * 性别枚举类
 * 该枚举类用于表示性别的三种可能：男、女和其他
 */
public enum Gender {
    // 男性，代码为1，描述为“男”
    MALE(1, "男"),
    // 女性，代码为0，描述为“女”
    FEMALE(0, "女"),
    // 其他性别，代码为2，描述为“其他”
    OTHER(2, "其他");

    @EnumValue
    private final int code;

    @JsonValue
    private final String description;

    /**
     * 性别的构造函数
     *
     * @param code 性别的代码
     * @param description 性别的描述
     */
    Gender(int code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 获取性别的代码
     *
     * @return 性别的代码
     */
    public int getCode() {
        return code;
    }

    /**
     * 获取性别的描述
     *
     * @return 性别的描述
     */
    public String getDescription() {
        return description;
    }
}


