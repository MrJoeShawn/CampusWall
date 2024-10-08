package com.campus.framework.dao.vo;


import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentVo {
    //评论ID
    private Integer commentId;
    //关联动态ID
    private Integer dynamicId;
    //评论用户ID
    private Integer userId;
    //评论用户名称
    private String username;
    //父评论ID，根评论为-1
    private Long parentCommentId;
    //所回复的评论的用户ID
    private Integer repliedUserId;
    //所回复的评论的用户名称
    private String repliedUsername;
    //回复的评论ID
    private Integer repliedCommentId;
    //评论内容
    private String content;
    //回复数
    private Integer replyCount;
    //用户头像URL
    private String userAvatar;
    //创建时间
    private Date createdAt;
    // 子评论
    private List<CommentVo> replies;
}
