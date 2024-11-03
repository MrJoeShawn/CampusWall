package com.campus.framework.dao.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import com.campus.framework.dao.entity.Tags;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DynamicVO {
    //评论ID
    private Integer commentId;
    //动态ID
    private Integer dynamicId;
    //发布用户ID
    private Integer userId;
    //动态摘要
    private String dynamicSummary;
    //动态封面URL
    private String dynamicCover;
    //动态内容，包含富文本和HTML标签
    private String dynamicContent;
    //清理后的动态内容，使用DOMPurify确保安全
    private String cleanedPostContent;
    //动态分类ID
    private Integer categoryId;
    //是否匿名发布 “0”否 “1”是 默认为否
    private Integer isAnonymous;
    //是否允许评论  “0”否 “1”是 默认为是
    private Integer allowComments;
    //是否为草稿 (0: 不是草稿, 1: 是草稿)
    private Integer isDraft;
    //浏览次数
    private Integer viewCount;
    //点赞次数
    private Integer likeCount;
    //收藏次数
    private Integer favoriteCount;
    //评论次数
    private Integer commentCount;
    //创建时间
    private Date createdAt;
    //全名
    private String fullName;
    //标签名称
    private List<Tags> tagName;
}
