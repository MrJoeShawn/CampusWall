package com.campus.framework.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.campus.framework.dao.entity.Tags;

import java.util.List;

/**
 * 动态标签表(Tags)表服务接口
 *
 * @author makejava
 * @since 2024-10-13 18:02:57
 */
public interface TagsService extends IService<Tags> {
    List<Tags> findTagsByNames(List<String> tagNames); // 批量查询标签
}

