package com.campus.framework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.framework.constants.SystemConstants;
import com.campus.framework.dao.entity.Dynamic;
import com.campus.framework.dao.entity.DynamicTags;
import com.campus.framework.dao.mapper.DynamicTagsMapper;
import com.campus.framework.dao.repository.ResponseResult;
import com.campus.framework.dao.vo.DynamicCategoriesVo;
import com.campus.framework.service.DynamicCategoriesService;
import com.campus.framework.service.DynamicService;
import com.campus.framework.untils.BeanCopyUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.campus.framework.dao.entity.DynamicCategories;
import com.campus.framework.dao.mapper.DynamicCategoriesMapper;

import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 动态分类表(DynamicCategories)表服务实现类
 *
 * @author makejava
 * @since 2024-10-03 14:30:22
 */
@Service("dynamicCategoriesService")
public class DynamicCategoriesServiceImpl extends ServiceImpl<DynamicCategoriesMapper, DynamicCategories> implements DynamicCategoriesService {

    @Autowired
    private DynamicService dynamicService;

    @Autowired
    private DynamicCategoriesMapper dynamicCategoriesMapper;

    /**
     * 查询分类列表
     * @return
     */
    @Override
    public ResponseResult getCategoryList() {
        //查询动态列表 状态为以发布的动态
        LambdaQueryWrapper<Dynamic> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dynamic::getIsDraft, SystemConstants.ARTICLE_STATUS_NOTDRAFT);
        queryWrapper.eq(Dynamic::getIsDeleted, SystemConstants.ARTICLE_STATUS_NOTDELETED);
        queryWrapper.eq(Dynamic::getIsPrivate,SystemConstants.DYNAMIC_STATUS_PUBLIC);
        List<Dynamic> DynamicList = dynamicService.list(queryWrapper);
        //获取动态的分类id,去重
        Set<Long> categoryid = DynamicList.stream()
                .map(dynamic -> Long.valueOf(dynamic.getCategoryId()))
                .collect(Collectors.toSet());
        //查询分类表
        List<DynamicCategories> dynamicCategories = listByIds(categoryid);
        dynamicCategories = dynamicCategories.stream().filter(category -> category.getStatus() == SystemConstants.CLASSIFICATION_NORMAL)
                .collect(Collectors.toList());
        //封装vo
        List<DynamicCategoriesVo> dynamicCategoriesVos = BeanCopyUtils.copyBeanList(dynamicCategories, DynamicCategoriesVo.class);

        return ResponseResult.okResult(dynamicCategoriesVos);
    }

    /**
     * 查询所有分类列表
     * @return
     */
    @Override
    public ResponseResult getAllCategoryList() {
        List<DynamicCategories> dynamicCategories = dynamicCategoriesMapper.selectList(null);
        return ResponseResult.okResult(dynamicCategories);
    }
}
