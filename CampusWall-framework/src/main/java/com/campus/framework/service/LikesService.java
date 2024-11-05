package com.campus.framework.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.framework.dao.entity.Likes;
import com.campus.framework.dao.repository.ResponseResult;

/**
 * 用户点赞表(Likes)表服务接口
 *
 * @author makejava
 * @since 2024-11-03 14:03:53
 */
public interface LikesService extends IService<Likes> {

    ResponseResult likeDynamic(Long userId, Long dynamicId);

    ResponseResult getLikeStatus(Integer pageNum, Integer pageSize, Integer userId);
}

