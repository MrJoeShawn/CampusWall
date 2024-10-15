package com.campus.framework.dao.entity;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 动态标签关联表(DynamicTags)表实体类
 *
 * @author makejava
 * @since 2024-10-13 18:02:10
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("dynamic_tags")
public class DynamicTags  {
    //动态ID
    private Long dynamicId;
    //标签ID
    private Long tagId;
}
