package com.campus.framework.dao.entity;


import java.util.Date;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 好友关系表(Friendship)表实体类
 *
 * @author makejava
 * @since 2025-03-09 23:05:14
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("friendship")
public class Friendship  {
    //关系ID
    @TableId
    private Integer id;
    //用户ID    
    private Integer userId;
    //好友ID    
    private Integer friendId;
    //好友状态
    private String status;  // 记录好友状态（待审核、已接受等）
    //创建时间    
    private Date createdAt;
}
