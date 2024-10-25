package com.campus.wall.web.controller;


import com.campus.framework.dao.repository.ResponseResult;
import com.campus.framework.service.DynamicTagsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/tag")
public class TagsContorller {


    @Autowired
    DynamicTagsService dynamicTagsService;

    @GetMapping("/getTagsList")
    public ResponseResult getTagsList() {
        return dynamicTagsService.getTagsList();
    }
}
