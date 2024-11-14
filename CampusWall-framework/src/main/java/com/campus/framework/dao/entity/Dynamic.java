package com.campus.framework.dao.entity;

import java.util.Date;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 用户动态表(Dynamic)表实体类
 *
 * @author makejava
 * @since 2024-11-14 15:38:31
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("dynamic")
public class Dynamic  {
    //动态ID
    @TableId
    private Integer dynamicId;
    //发布用户ID    
    private Integer userId;
    //动态摘要    
    private String dynamicSummary;
    //动态内容，包含富文本和HTML标签    
    private String dynamicContent;
    //清理后的动态内容，使用DOMPurify确保安全    
    private String cleanedPostContent;
    //动态分类ID    
    private Integer categoryId;
    //动态封面URL    
    private String dynamicCover;
    //是否匿名发布 “0”否 “1”是 默认为否    
    private Integer isAnonymous;
    //是否允许评论  “0”否 “1”是 默认为是    
    private Integer allowComments;
    //是否为草稿 (0: 不是草稿, 1: 是草稿)    
    private Integer isDraft;
    //是否已删除 “0”否 “1”是 默认为否    
    private Integer isDeleted;
    //是否置顶  “0”否 “1”是 默认为否    
    private Integer isTop;
    //是否仅自己可见 (0: 不是, 1: 是)    
    private Integer isPrivate;
    //浏览次数    
    private Integer viewCount;
    //点赞次数    
    private Integer likeCount;
    //收藏次数    
    private Integer favoriteCount;
    //评论次数    
    private Integer commentCount;
    //更新时间    
    private Date updatedAt;
    //创建时间    
    private Date createdAt;
    //创建人ID    
    private Long createdBy;
    //更新人ID    
    private Long updatedBy;
}
