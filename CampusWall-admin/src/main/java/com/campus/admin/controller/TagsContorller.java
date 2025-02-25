package com.campus.admin.controller;

import com.campus.framework.dao.repository.ResponseResult;
import com.campus.framework.service.DynamicTagsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/tag")
public class TagsContorller {


    @Autowired
    DynamicTagsService dynamicTagsService;

    /**
     * 获取所有标签
     * @return
     */
    @GetMapping("/getTagsList")
    public ResponseResult getTagsList() {
        return dynamicTagsService.getTagsList();
    }
}