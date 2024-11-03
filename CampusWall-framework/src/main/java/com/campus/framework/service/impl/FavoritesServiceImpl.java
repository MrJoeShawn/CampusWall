package com.campus.framework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.framework.dao.entity.Dynamic;
import com.campus.framework.dao.entity.Favorites;
import com.campus.framework.dao.mapper.DynamicMapper;
import com.campus.framework.dao.mapper.FavoritesMapper;
import com.campus.framework.dao.repository.ResponseResult;
import com.campus.framework.service.FavoritesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 用户收藏表(Favorites)表服务实现类
 *
 * @author makejava
 * @since 2024-11-03 15:16:57
 */
@Service("favoritesService")
public class FavoritesServiceImpl extends ServiceImpl<FavoritesMapper, Favorites> implements FavoritesService {

    @Autowired
    private DynamicMapper dynamicMapper;

    @Override
    public ResponseResult collectDynamic(Long userId, Long dynamicId) {
        // 查询用户是否已经收藏

        Favorites existingcollect = getOne(new LambdaQueryWrapper<Favorites>()
                .eq(Favorites::getUserId, userId)
                .eq(Favorites::getDynamicId, dynamicId));
        if (existingcollect != null) {
            // 用户已经收藏，取消收藏
            removeById(existingcollect.getFavoriteId());
            updatecollectCount(dynamicId, -1);
            return ResponseResult.okResult("取消收藏成功");
        }else {
            // 用户没有收藏，添加新的收藏记录
            Favorites newcollect = new Favorites();
            newcollect.setUserId(userId);
            newcollect.setDynamicId(dynamicId);
            save(newcollect); // 保存新的收藏记录
            updatecollectCount(dynamicId, 1); // 更新动态的收藏数增加
            return ResponseResult.okResult("添加收藏成功");
        }
    }

    /**
     * 更新动态的收藏数
     * @param dynamicId
     * @param i
     */
    private void updatecollectCount(Long dynamicId, int i) {
        UpdateWrapper<Dynamic> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("dynamic_id", dynamicId);
        updateWrapper.setSql("favorite_count = favorite_count + " + i);
        dynamicMapper.update(null, updateWrapper);
    }
}
