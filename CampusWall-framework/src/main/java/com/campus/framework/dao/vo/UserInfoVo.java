package com.campus.framework.dao.vo;

import com.campus.framework.dao.enums.Gender;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;

@Data
@Accessors(chain = true)
public class UserInfoVo {
    //用户ID
    private Integer id;
    //用户名
    private String username;
    //密码（加密后）
    private String password;
    //电子邮件
    private String email;
    //电话号码
    private String phoneNumber;
    //全名
    private String fullName;
    //性别  'Male男-1','Female女-0','Other其他-2'
    private Gender gender;
    //生日
    private Date birthdate;
    //地址
    private String address;
    //头像URL
    private String profilePictureUrl;
    //学校名称
    private String school;
    //专业
    private String major;
}
