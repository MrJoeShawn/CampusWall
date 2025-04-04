package com.campus.framework.dao.vo;

import com.campus.framework.dao.entity.Dynamic;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDynamicListVO {
    //动态ID
    private Integer dynamicId;
    //发布用户ID
    private Integer userId;
    //动态摘要
    private String dynamicSummary;
    //动态内容
    private String cleanedPostContent;
    //动态封面URL
    private String dynamicCover;
    //浏览次数
    private Integer viewCount;
    //点赞次数
    private Integer likeCount;
    //收藏次数
    private Integer favoriteCount;
    //评论次数
    private Integer commentCount;
    //发布时间
    private Date createdAt;
    //是否匿名发布 “0”否 “1”是 默认为否
    private Integer isAnonymous;
    //是否为草稿 (0: 不是草稿, 1: 是草稿)
    private Integer isDraft;
    //是否置顶 (0: 不是置顶, 1: 是置顶)
    private Integer isTop;
    //是否为私密动态 (0: 不是私密动态, 1: 是私密动态)
    private Integer isPrivate;
    //动态
    private List<Dynamic> TopDynamic;
}
