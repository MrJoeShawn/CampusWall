package com.campus.framework.dao.entity;

import java.util.Date;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 用户表(Users)表实体类
 *
 * @author makejava
 * @since 2024-11-10 16:10:54
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("users")
public class Users  {
    //用户ID
    @TableId
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
    //是否启用账户    
    private Integer enabled;
    //账户是否未过期    
    private Integer accountNonExpired;
    //凭证是否未过期    
    private Integer credentialsNonExpired;
    //账户是否未锁定    
    private Integer accountNonLocked;
    //邮箱是否验证    
    private Integer emailVerified;
    //最后登录时间    
    private Date lastLoginTime;
    //创建时间    
    private Date createdAt;
    //更新时间    
    private Date updatedAt;
}
