package com.campus.wall.web.controller;

import com.campus.framework.dao.entity.Comments;
import com.campus.framework.dao.entity.Dynamic;
import com.campus.framework.dao.repository.ResponseResult;
import com.campus.framework.dao.vo.DynamicVO;
import com.campus.framework.service.DynamicService;
import com.campus.framework.untils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dynamic")
public class DynamicController {

    @Autowired
    private DynamicService dynamicService;

    /**
     * 发布动态
     * @param dynamicVO
     * @return
     */
    @PostMapping("/create")
    public ResponseResult createDynamic(@RequestBody DynamicVO dynamicVO) {
        System.out.println("dynamic = " + dynamicVO);
        return dynamicService.createDynamic(dynamicVO);
    }

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
     * 用户主页展示用户信息
     * @param userId
     * @return
     */
    @GetMapping("/HomepageUserInfo/{id}")
    public ResponseResult getUserInfoByUserId(@PathVariable(value = "id", required = false) Integer userId) {
        return dynamicService.getUserInfoByUserId(userId);
    }

    /**
     * 用户主页展示用户信息  从token中获取用户id，如果未提供id
     * @return
     */
    @GetMapping("/HomepageUserInfo")
    public ResponseResult getCurrentUserInfo() {
        Integer userId = SecurityUtils.getUserId();
        return dynamicService.getUserInfoByUserId(userId);
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

    /**
     * 获取置顶动态 用户点击自己主页
     * @return
     */
    @GetMapping("/topdynamics")
    public ResponseResult getTopDynamics() {
        Integer userId = SecurityUtils.getUserId();
        return dynamicService.getDynamicTop(userId);
    }

    /**
     * 获取置顶动态 用户点击其他用户主页
     * @return
     */
    @GetMapping("/topdynamics/{userId}")
    public ResponseResult getTopDynamicsByUserId(@PathVariable Integer userId) {
        return dynamicService.getDynamicTop(userId);
    }

    /**
     * 设置置顶动态
     * @param dynamicId
     * @return
     */
    @PutMapping("/topdynamics")
    public ResponseResult updateTopDynamics(@RequestParam Integer dynamicId) {
        return dynamicService.updateTopDynamics(dynamicId);
    }


    @GetMapping("/searchDynamic")
    public ResponseResult selectByDynamicSummary(String dynamicSummary,Integer pageNum, Integer pageSize) {
        return dynamicService.selectByDynamicSummary(dynamicSummary,pageNum,pageSize);
    }
}
