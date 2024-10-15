package com.campus.framework.dao.entity;

import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 动态标签表(Tags)表实体类
 *
 * @author makejava
 * @since 2024-10-13 18:01:32
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("tags")
public class Tags  {
    //标签ID
    @TableId
    private Long tagId;
    //标签名称    
    private String tagName;
}
