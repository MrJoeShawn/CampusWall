package com.campus.framework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.framework.constants.SystemConstants;
import com.campus.framework.dao.entity.Dynamic;
import com.campus.framework.dao.mapper.DynamicMapper;
import com.campus.framework.dao.repository.ResponseResult;
import com.campus.framework.dao.vo.DynamicListVO;
import com.campus.framework.dao.vo.DynamicVO;
import com.campus.framework.dao.vo.PageVo;
import com.campus.framework.service.DynamicService;
import com.campus.framework.untils.BeanCopyUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DynamicServiceImpl extends ServiceImpl<DynamicMapper, Dynamic> implements DynamicService {

    /**
     * 首页获取动态列表
     * @return
     */
    @Override
    public ResponseResult getDynamicList(Integer pageNum,Integer pageSize) {
        LambdaQueryWrapper<Dynamic> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dynamic::getIsDeleted, SystemConstants.ARTICLE_STATUS_DRAFT);
        queryWrapper.eq(Dynamic::getIsDraft, SystemConstants.ARTICLE_STATUS_NORMAL);
        queryWrapper.orderByDesc(Dynamic::getCreatedAt);
        Page<Dynamic> page = new Page<>(pageNum, pageSize);
        page(page, queryWrapper);
        List<Dynamic> dynamicList = page.getRecords();
        List<DynamicListVO> dynamicsListVo = BeanCopyUtils.copyBeanList(dynamicList, DynamicListVO.class);
        return ResponseResult.okResult(dynamicsListVo);
    }

    /**
     * 分类获取动态列表
     * @param pageNum
     * @param pageSize
     * @param categoryId
     * @return
     */
    @Override
    public ResponseResult getDynamicListByCategoryId(Integer pageNum, Integer pageSize, Long categoryId) {
        LambdaQueryWrapper<Dynamic> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dynamic::getIsDeleted, SystemConstants.ARTICLE_STATUS_DRAFT);
        queryWrapper.eq(Dynamic::getIsDraft, SystemConstants.ARTICLE_STATUS_NORMAL);
        if (categoryId != null) {
            queryWrapper.eq(Dynamic::getCategoryId, categoryId);
        }
        queryWrapper.orderByDesc(Dynamic::getCreatedAt);
        Page<Dynamic> page = new Page<>(pageNum, pageSize);
        page(page, queryWrapper);
        List<DynamicListVO> dynamicsListVo = BeanCopyUtils.copyBeanList(page.getRecords(), DynamicListVO.class);

        PageVo pageVo = new PageVo(dynamicsListVo, page.getTotal());
        return ResponseResult.okResult(pageVo);
    }

    @Override
    public ResponseResult getDynamicById(Long id) {
        Dynamic dynamic = getById(id);
        DynamicVO dynamicVO = BeanCopyUtils.copyBean(dynamic, DynamicVO.class);
        return ResponseResult.okResult(dynamicVO);
    }
}
