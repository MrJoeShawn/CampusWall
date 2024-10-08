package com.campus.framework.dao.entity;

import java.util.Date;
import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 评论表(Comments)表实体类
 *
 * @author makejava
 * @since 2024-10-05 20:41:27
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("comments")
public class Comments  {
    //评论ID
    @TableId
    private Integer commentId;
    //关联动态ID    
    private Integer dynamicId;
    //评论用户ID    
    private Integer userId;
    //父评论ID，根评论为-1    
    private Long parentCommentId;
    //所回复的评论的用户ID    
    private Integer repliedUserId;
    //回复的评论ID    
    private Integer repliedCommentId;
    //评论内容    
    private String content;
    //回复数    
    private Integer replyCount;
    //用户头像URL    
    private String userAvatar;
    //是否已删除    
    private Integer isDeleted;
    //创建人
    @TableField(fill = FieldFill.INSERT)
    private Long createdBy;
    //更新人
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updatedBy;
    //创建时间
    @TableField(fill = FieldFill.INSERT)
    private Date createdAt;
    //更新时间
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updatedAt;
}
