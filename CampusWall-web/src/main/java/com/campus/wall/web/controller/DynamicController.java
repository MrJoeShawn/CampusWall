package com.campus.wall.web.controller;

import com.campus.framework.dao.repository.ResponseResult;
import com.campus.framework.service.DynamicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dynamic")
public class DynamicController {

    @Autowired
    private DynamicService dynamicService;

    /**
     * 首页获取动态列表
     * @return
     */
    @GetMapping("/list")
    public ResponseResult getDynamicList(Integer pageNum,Integer pageSize) {
        ResponseResult result = dynamicService.getDynamicList(pageNum,pageSize);
        return result;
    }

    /**
     * 根据分类id获取动态列表
     * @param pageNum
     * @param pageSize
     * @param categoryId
     * @return
     */
    @GetMapping("/listByCategoryId")
        public ResponseResult getDynamicListByCategoryId(Integer pageNum,Integer pageSize,Long categoryId) {
        return dynamicService.getDynamicListByCategoryId(pageNum,pageSize,categoryId);
    }

    @GetMapping("/{id}")
    public ResponseResult getDynamicById(@PathVariable("id") Long id) {
        return dynamicService.getDynamicById(id);
    }

}
