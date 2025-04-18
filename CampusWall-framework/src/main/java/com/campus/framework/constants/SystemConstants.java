package com.campus.framework.constants;

import com.campus.framework.dao.enums.AppHttpCodeEnum;

public class SystemConstants {
    /**
     *  动态被删除
     */
    public static final int ARTICLE_STATUS_DELETED = 1;

    /**
     *  动态未删除
     */
    public static final int ARTICLE_STATUS_NOTDELETED = 0;

    /**
     *  动态是草稿
     */
    public static final int ARTICLE_STATUS_DRAFT = 1;

    /**
     * 动态不是草稿
     */
    public static final int ARTICLE_STATUS_NOTDRAFT = 0;

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

    /**
     * 动态状态为仅自己可见
     */
    public static final int DYNAMIC_STATUS_PRIVATE=1;

    /**
     * 动态状态为公开
     */
    public static final int DYNAMIC_STATUS_PUBLIC=0;


}
