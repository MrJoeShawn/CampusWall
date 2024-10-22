package com.campus.wall.web.controller;

import com.campus.framework.dao.repository.ResponseResult;
import com.campus.framework.service.DynamicService;
import com.campus.framework.untils.SecurityUtils;
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

    /**
     * 展示动态详情 首页点击后展示动态详情
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public ResponseResult getDynamicById(@PathVariable("id") Long id) {
        return dynamicService.getDynamicById(id);
    }


    /**
     * 根据动态id获取用户信息
     * @param dynamicId
     * @return
     */
    @GetMapping("/user/{id}")
    public ResponseResult getUserByDynamicId(@PathVariable("id") Integer dynamicId) {
        return dynamicService.getUserByDynamicId(dynamicId);
    }

    /**
     * 根据用户id获取动态列表 个人主页动态 从token中获取用户id
     * @return
     */
    @GetMapping("/user/profile/dynamics")
    public ResponseResult getDynamicByUserId(Integer pageNum, Integer pageSize) {
        Integer userId = SecurityUtils.getUserId();
        return dynamicService.getDynamicListByUserId(pageNum, pageSize,userId);
    }

    /**
     * 根据用户id获取动态列表 用户主页动态 作品页面获取用户动态列表
     * @return
     */
    @GetMapping("/users/{userId}/dynamics")
    public ResponseResult getUserDynamics(@PathVariable Integer userId, Integer pageNum, Integer pageSize) {
        return dynamicService.getDynamicListByUserId(pageNum, pageSize, userId);
    }
}
