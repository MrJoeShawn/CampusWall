package com.campus.wall.web.controller;

import com.campus.framework.dao.repository.ResponseResult;
import com.campus.framework.service.DynamicCategoriesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/categories")
public class DynamicCategoriesController {

    @Autowired
    private DynamicCategoriesService dynamicCategoriesService;

    /**
     * 获取动态分类列表
     * @return
     */
    @GetMapping("/getCategoryList")
    public ResponseResult getCategoryList() {
        return dynamicCategoriesService.getCategoryList();
    }
}
