package com.campus.framework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.framework.dao.entity.Tags;
import com.campus.framework.dao.mapper.TagsMapper;
import com.campus.framework.service.TagsService;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * 动态标签表(Tags)表服务实现类
 *
 * @author makejava
 * @since 2024-10-13 18:02:58
 */
@Service("tagsService")
public class TagsServiceImpl extends ServiceImpl<TagsMapper, Tags> implements TagsService {
    @Override
    public List<Tags> findTagsByNames(List<String> tagNames) {
        LambdaQueryWrapper<Tags> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Tags::getTagName, tagNames); // 根据标签名称批量查询
        return list(queryWrapper);
    }
}
