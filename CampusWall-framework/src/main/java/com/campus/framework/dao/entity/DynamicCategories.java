package com.campus.framework.dao.entity;

import java.util.Date;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 动态分类表(DynamicCategories)表实体类
 *
 * @author makejava
 * @since 2024-10-03 14:20:24
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("dynamic_categories")
public class DynamicCategories  {
    //分类ID
    @TableId
    private Integer categoryId;
    //分类名称    
    private String categoryName;
    //分类描述    
    private String categoryDescription;
    //父分类ID  没有父分类，则该字段为空。    
    private Integer parentCategoryId;
    //状态，1表示启用，0表示禁用    
    private Integer status;
    //创建时间    
    private Date createdAt;
    //更新时间    
    private Date updatedAt;
    //删除时间    
    private Date deletedAt;
    //是否已删除 0未删除 1 删除    
    private Integer isDeleted;
}
