package com.campus.framework.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.framework.dao.mapper.DynamicTagsMapper;
import com.campus.framework.service.DynamicTagsService;
import org.springframework.stereotype.Service;
import com.campus.framework.dao.entity.DynamicTags;

/**
 * 动态标签关联表(DynamicTags)表服务实现类
 *
 * @author makejava
 * @since 2024-10-13 18:03:36
 */
@Service("dynamicTagsService")
public class DynamicTagsServiceImpl extends ServiceImpl<DynamicTagsMapper, DynamicTags> implements DynamicTagsService {

}
