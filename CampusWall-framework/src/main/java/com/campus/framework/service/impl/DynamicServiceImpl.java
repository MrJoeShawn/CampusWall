package com.campus.framework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.framework.constants.SystemConstants;
import com.campus.framework.dao.entity.Dynamic;
import com.campus.framework.dao.mapper.DynamicMapper;
import com.campus.framework.dao.repository.ResponseResult;
import com.campus.framework.dao.vo.DynamicListVO;
import com.campus.framework.service.DynamicService;
import com.campus.framework.untils.BeanCopyUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DynamicServiceImpl extends ServiceImpl<DynamicMapper, Dynamic> implements DynamicService {
    @Override
    public ResponseResult getDynamicList() {
        LambdaQueryWrapper<Dynamic> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dynamic::getIsDeleted, SystemConstants.ARTICLE_STATUS_DRAFT);
        queryWrapper.eq(Dynamic::getIsDraft, SystemConstants.ARTICLE_STATUS_NORMAL);
        queryWrapper.orderByDesc(Dynamic::getCreatedAt);
        Page<Dynamic> page = new Page<>();
        page(page, queryWrapper);
        List<Dynamic> dynamicList = page.getRecords();

//        List<DynamicListVO> dynamicsListVo = new ArrayList<>();
//        for (Dynamic dynamic : dynamicList) {
//            DynamicListVO dynamicVo = new DynamicListVO();
//            BeanUtils.copyProperties(dynamic, dynamicVo);
//            dynamicsListVo.add(dynamicVo);
//        }

        List<DynamicListVO> dynamicsListVo = BeanCopyUtils.copyBeanList(dynamicList, DynamicListVO.class);
        return ResponseResult.okResult(dynamicsListVo);
    }
}
