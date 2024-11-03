package com.campus.framework.dao.entity;

import java.util.Date;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 用户点赞表(Likes)表实体类
 *
 * @author makejava
 * @since 2024-11-03 14:03:33
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("likes")
public class Likes  {
    //点赞ID
    @TableId
    private Long likeId;
    //用户ID    
    private Long userId;
    //动态ID    
    private Long dynamicId;
    //点赞时间    
    private Date createdAt;
}
