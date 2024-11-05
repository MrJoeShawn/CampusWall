package com.campus.framework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.campus.framework.dao.entity.Dynamic;
import com.campus.framework.dao.entity.Likes;
import com.campus.framework.dao.mapper.DynamicMapper;
import com.campus.framework.dao.mapper.LikesMapper;
import com.campus.framework.dao.repository.ResponseResult;
import com.campus.framework.dao.vo.DynamicListVO;
import com.campus.framework.dao.vo.PageVo;
import com.campus.framework.service.DynamicService;
import com.campus.framework.service.LikesService;
import com.campus.framework.untils.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;


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

    @Autowired
    DynamicService dynamicsService;

    /**
     * 用户点赞
     * @param userId
     * @param dynamicId
     * @return
     */
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
     * 查询用户喜欢的动态
     * @param pageNum
     * @param pageSize
     * @param userId
     * @return
     */
    @Override
    public ResponseResult getLikeStatus(Integer pageNum, Integer pageSize, Integer userId) {
        // 创建查询条件，查询当前用户的所有喜欢记录
        QueryWrapper<Likes> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.orderByDesc("created_at");
        List<Likes> likesList = list(queryWrapper);

        // 提取用户喜欢的动态ID
        List<Long> dynamicIds = likesList.stream()
                .map(Likes::getDynamicId)
                .collect(Collectors.toList());

        // 如果没有喜欢的动态，直接返回空列表
        if (dynamicIds.isEmpty()) {
            return ResponseResult.okResult(Collections.emptyList());
        }

        // 查询动态详情时使用分页
        QueryWrapper<Dynamic> dynamicsQueryWrapper = new QueryWrapper<>();
        dynamicsQueryWrapper.in("dynamic_id", dynamicIds);
        dynamicsQueryWrapper.orderByDesc("created_at");


        // 使用分页查询，避免一次性加载所有数据
        Page<Dynamic> page = new Page<>(pageNum, pageSize);
        // 确保分页对象类型与查询条件实体类型一致
        dynamicsService.page(page, dynamicsQueryWrapper);

        // 将查询结果转换为VO列表
        List<DynamicListVO> dynamicListVOS = page.getRecords().stream()
                .map(dynamic -> BeanCopyUtils.copyBean(dynamic, DynamicListVO.class))
                .collect(Collectors.toList());

        // 封装分页结果
        PageVo pageVo = new PageVo(dynamicListVOS, page.getTotal());
        // 返回封装结果
        return ResponseResult.okResult(pageVo);
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
