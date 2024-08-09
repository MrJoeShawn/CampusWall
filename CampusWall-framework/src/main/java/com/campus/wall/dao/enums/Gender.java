package com.campus.wall.dao.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Gender {
    MALE(1, "男"),
    FEMALE(0, "女"),
    OTHER(2, "其他");

    @EnumValue
    private final int code;

    @JsonValue
    private final String description;

    Gender(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }
}

