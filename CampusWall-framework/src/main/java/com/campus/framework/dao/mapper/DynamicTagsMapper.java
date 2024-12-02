package com.campus.framework.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.framework.dao.entity.DynamicTags;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

/**
 * 动态标签关联表(DynamicTags)表数据库访问层
 *
 * @author makejava
 * @since 2024-10-13 18:02:10
 */
public interface DynamicTagsMapper extends BaseMapper<DynamicTags> {
    // 根据 dynamicId 删除所有关联的标签关系
    @Delete("DELETE FROM dynamic_tags WHERE dynamic_id = #{dynamicId}")
    int deleteByDynamicId(Integer dynamicId);
}
