package com.campus.wall.web.controller;

import com.campus.framework.dao.repository.ResponseResult;
import com.campus.framework.service.DynamicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dynamic")
public class DynamicController {

    @Autowired
    private DynamicService dynamicService;

//    @GetMapping("/list")
//    public List<Dynamic> dynamicList(){
//        return dynamicService.list();
//    }

    @GetMapping("/list")
    public ResponseResult getDynamicList() {
        ResponseResult result = dynamicService.getDynamicList();
        return result;
    }
}
