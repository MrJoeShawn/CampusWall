package com.campus.framework.dao.vo;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Date;
import java.util.List;


@Data
@Accessors(chain = true)
public class UserInfoVo {
    //用户ID
    private Integer id;
    //用户名
    private String username;
    //电子邮件
    private String email;
    //电话号码
    private String phoneNumber;



    //全名
    private String fullName;
    //性别  '男-1','女-0','其他-2'
    private String gender;
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
    //热门动态
    private List hotDynamic;
    // 是否为好友
    private boolean isFriend;  // 属性名保持小写的isFriend
    public boolean isFriend() {
        return isFriend;
    }

    public void setFriend(boolean friend) {
        isFriend = friend;
    }
}
