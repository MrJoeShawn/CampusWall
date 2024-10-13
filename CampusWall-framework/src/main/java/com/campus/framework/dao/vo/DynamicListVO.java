package com.campus.framework.dao.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DynamicListVO {
    //动态ID
    private Integer dynamicId;
    //发布用户ID
    private Integer userId;
    //动态摘要
    private String dynamicSummary;
    //动态封面URL
    private String dynamicCover;
    //发布时间
    private Date createdAt;
    //全名
    private String fullName;
}
