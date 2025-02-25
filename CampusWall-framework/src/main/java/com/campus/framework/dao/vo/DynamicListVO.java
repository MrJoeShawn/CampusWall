package com.campus.framework.dao.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DynamicListVO {
    //动态ID
    private Integer dynamicId;
    //发布用户ID
    private Integer userId;
    //动态摘要
    private String dynamicSummary;
    //动态封面URL
    private String dynamicCover;
    //发布时间
    private Date createdAt;
    //全名
    private String fullName;
    //头像URL
    private String profilePictureUrl;
    //浏览次数
    private Integer viewCount;
    //点赞次数
    private Integer likeCount;
    //收藏次数
    private Integer favoriteCount;
    //评论次数
    private Integer commentCount;
    //是否置顶  “0”否 “1”是 默认为否
    private Integer isTop;
}
