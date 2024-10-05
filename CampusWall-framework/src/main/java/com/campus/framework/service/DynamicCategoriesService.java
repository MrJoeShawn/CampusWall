package com.campus.framework.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.framework.dao.entity.DynamicCategories;
import com.campus.framework.dao.repository.ResponseResult;


/**
 * 动态分类表(DynamicCategories)表服务接口
 *
 * @author makejava
 * @since 2024-10-03 14:22:57
 */
public interface DynamicCategoriesService extends IService<DynamicCategories> {

    ResponseResult getCategoryList();
}

