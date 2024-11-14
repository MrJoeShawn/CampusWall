package com.campus.framework.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.framework.dao.entity.Favorites;
import com.campus.framework.dao.repository.ResponseResult;

/**
 * 用户收藏表(Favorites)表服务接口
 *
 * @author makejava
 * @since 2024-11-03 15:16:57
 */
public interface FavoritesService extends IService<Favorites> {

    ResponseResult collectDynamic(Long userId, Long dynamicId);

    ResponseResult getCollectStatus(Integer pageNum, Integer pageSize, Integer userId);

    ResponseResult getDraftStatus(Integer pageNum, Integer pageSize, Integer userId);
}

