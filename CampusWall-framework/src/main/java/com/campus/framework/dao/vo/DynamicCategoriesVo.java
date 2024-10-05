package com.campus.framework.dao.vo;

import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DynamicCategoriesVo {
    //分类ID
    private Integer categoryId;
    //分类名称
    private String categoryName;
}
