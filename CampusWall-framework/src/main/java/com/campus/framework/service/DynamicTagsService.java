package com.campus.framework.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.framework.dao.entity.DynamicTags;
import com.campus.framework.dao.repository.ResponseResult;


/**
 * 动态标签关联表(DynamicTags)表服务接口
 *
 * @author makejava
 * @since 2024-10-13 18:03:36
 */
public interface DynamicTagsService extends IService<DynamicTags> {

    ResponseResult getTagsList();

    void removeByDynamicId(Integer dynamicId);
}

