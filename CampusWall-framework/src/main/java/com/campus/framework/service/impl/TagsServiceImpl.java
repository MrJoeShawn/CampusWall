package com.campus.framework.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.framework.dao.entity.Tags;
import com.campus.framework.dao.mapper.TagsMapper;
import com.campus.framework.service.TagsService;
import org.springframework.stereotype.Service;


/**
 * 动态标签表(Tags)表服务实现类
 *
 * @author makejava
 * @since 2024-10-13 18:02:58
 */
@Service("tagsService")
public class TagsServiceImpl extends ServiceImpl<TagsMapper, Tags> implements TagsService {

}
