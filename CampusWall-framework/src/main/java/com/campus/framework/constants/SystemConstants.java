package com.campus.framework.constants;

import com.campus.framework.dao.enums.AppHttpCodeEnum;

public class SystemConstants {
    /**
     *  文章是草稿
     */
    public static final int ARTICLE_STATUS_DRAFT = 0;
    /**
     *  文章是正常分布状态
     */
    public static final int ARTICLE_STATUS_NORMAL = 0;

    /**
     *  文章分类是正常状态
     */
    public static final int CLASSIFICATION_NORMAL = 1;

    /**
     *  评论是否为根评论
     */
    public static final int COMMENT_ISROOT = -1;

    /**
     * 文章是否置顶
     */
    public static final int ARTICLE_STATUS_TOP=1;


    /**
     * 用户喜欢这条动态
     */
    public static final int LIKE_STATUS_YES=1;

    /**
     * 用户不喜欢这条动态
     */
    public static final int LIKE_STATUS_NO=0;

    /**
     * 用户收藏了动态
     */
    public static final int COLLECT_STATUS_YES=1;

    /**
     * 用户没有收藏动态
     */
    public static final int COLLECT_STATUS_NO=0;
}
