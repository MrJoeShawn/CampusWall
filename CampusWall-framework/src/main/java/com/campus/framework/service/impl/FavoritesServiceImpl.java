package com.campus.framework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.framework.dao.entity.Dynamic;
import com.campus.framework.dao.entity.Favorites;
import com.campus.framework.dao.mapper.DynamicMapper;
import com.campus.framework.dao.mapper.FavoritesMapper;
import com.campus.framework.dao.repository.ResponseResult;
import com.campus.framework.dao.vo.DynamicListVO;
import com.campus.framework.dao.vo.PageVo;
import com.campus.framework.service.DynamicService;
import com.campus.framework.service.FavoritesService;
import com.campus.framework.untils.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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

    @Autowired
    private DynamicService dynamicService;

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

    @Override
    public ResponseResult getCollectStatus(Integer pageNum, Integer pageSize, Integer userId) {
        // 查询用户收藏的动态
        QueryWrapper<Favorites> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        queryWrapper.orderByDesc("created_at");
        List<Favorites> favoritesList = list(queryWrapper);
        // 提取用户收藏的动态ID
        List<Long> dynamicIds = favoritesList.stream()
                .map(Favorites::getDynamicId)
                .collect(Collectors.toList());
        if(dynamicIds.isEmpty()){
            return ResponseResult.okResult(Collections.emptyList());
        }

        // 查询动态详情时使用分页
        QueryWrapper<Dynamic> dynamicsQueryWrapper = new QueryWrapper<>();
        dynamicsQueryWrapper.in("dynamic_id", dynamicIds);
        dynamicsQueryWrapper.orderByDesc("created_at");
        Page<Dynamic> page = new Page<>(pageNum, pageSize);
        dynamicService.page(page, dynamicsQueryWrapper);
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
