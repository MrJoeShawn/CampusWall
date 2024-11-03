package com.campus.framework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.framework.dao.entity.Dynamic;
import com.campus.framework.dao.entity.Likes;
import com.campus.framework.dao.mapper.DynamicMapper;
import com.campus.framework.dao.mapper.LikesMapper;
import com.campus.framework.dao.repository.ResponseResult;
import com.campus.framework.service.LikesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 用户点赞表(Likes)表服务实现类
 *
 * @author makejava
 * @since 2024-11-03 14:03:53
 */
@Service("likesService")
public class LikesServiceImpl extends ServiceImpl<LikesMapper, Likes> implements LikesService {

    @Autowired
    private DynamicMapper dynamicMapper;

    @Override
    public ResponseResult likeDynamic(Long userId, Long dynamicId) {
        // 查询用户是否已经点赞
        Likes existingLike = getOne(new LambdaQueryWrapper<Likes>()
                .eq(Likes::getUserId, userId)
                .eq(Likes::getDynamicId, dynamicId));

        if (existingLike != null) {
            // 用户已经点赞，取消点赞
            removeById(existingLike.getLikeId());
            updateLikeCount(dynamicId, -1); // 更新动态的喜欢数减少
            return ResponseResult.okResult("取消喜欢成功");
        } else {
            // 用户没有点赞，添加新的点赞记录
            Likes newLike = new Likes();
            newLike.setUserId(userId);
            newLike.setDynamicId(dynamicId);
            save(newLike); // 保存新的点赞记录
            updateLikeCount(dynamicId, 1); // 更新动态的喜欢数增加
            return ResponseResult.okResult("添加喜欢成功");
        }
    }



    /**
     * 更新动态的喜欢数
     * @param dynamicId
     * @param increment
     */
    private void updateLikeCount(Long dynamicId, int increment) {
        UpdateWrapper<Dynamic> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("dynamic_id", dynamicId);
        updateWrapper.setSql("like_count = like_count + " + increment);
        dynamicMapper.update(null, updateWrapper); // 执行更新
    }
}
