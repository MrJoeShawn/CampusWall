package com.campus.admin.controller;

import com.campus.framework.dao.repository.ResponseResult;
import com.campus.framework.dao.vo.DynamicVO;
import com.campus.framework.service.DynamicCategoriesService;
import com.campus.framework.service.DynamicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin")
public class DynamicController {

    @Autowired
    private DynamicCategoriesService dynamicCategoriesService;

    @Autowired
    private DynamicService dynamicService;

    /**
     * admin首页展示校园公告
     * @return
     */
    @GetMapping("/getCampusAnnouncements")
    public ResponseResult getCampusAnnouncements(){
        return dynamicCategoriesService.getCampusAnnouncementsList();
    }


    /**
     * admin管理所有校园公告
     * @return
     */
    @GetMapping("/getAdminCampusAnnouncements")
    public ResponseResult getAdminCampusAnnouncements(Integer pageNum,Integer pageSize){
        return dynamicCategoriesService.getAdminCampusAnnouncementsList(pageNum,pageSize);
    }


    /**
     * 更新公告的置顶状态
     */
    @PutMapping("/updateStatus")
    public ResponseResult updateAnnouncementStatus(@RequestParam Integer dynamicId,Integer isTop) {
        return dynamicCategoriesService.updateTopDynamics(dynamicId,isTop);
    }

    /**
     * 发布校园公告
     * @param dynamicVO
     * @return
     */
    @PostMapping("/createCampusAnnouncements")
    public ResponseResult createDynamic(@RequestBody DynamicVO dynamicVO) {
        System.out.println("dynamic = " + dynamicVO);
        return dynamicService.createDynamic(dynamicVO);
    }


    /**
     * 展示要修改的动态
     * @param dynamicId
     * @return
     */
    @GetMapping("/update/{dynamicId}")
    public ResponseResult updateGetDynamic(@PathVariable Integer dynamicId) {
        return dynamicService.updateGetDynamic(dynamicId);
    }

    /**
     * 删除校园公告
     * @param dynamicId
     * @return
     */
    @PutMapping("/delete")
    public ResponseResult deleteDynamic(@RequestParam Integer dynamicId) {
        return dynamicService.deleteDynamic(dynamicId);
    }



    /**
     * 获取动态列表（后台管理系统）
     * @param pageNum 页码
     * @param pageSize 每页数量
     * @return 返回动态列表
     */
    @GetMapping("/dynamic/list")
    public ResponseResult getAdminDynamicList(@RequestParam Integer pageNum, @RequestParam Integer pageSize) {
        return dynamicService.getDynamicList(pageNum, pageSize);
    }

    /**
     * 获取动态详情（后台管理系统）
     * @param dynamicId 动态ID
     * @return 返回动态详情
     */
    @GetMapping("/dynamic/{dynamicId}")
    public ResponseResult getAdminDynamicById(@PathVariable Long dynamicId) {
        return dynamicService.getDynamicById(dynamicId);
    }

    /**
     * 创建动态（后台管理系统）
     * @param dynamicVO 动态信息
     * @return 返回创建的结果
     */
    @PostMapping("/dynamic/create")
    public ResponseResult createAdminDynamic(@RequestBody DynamicVO dynamicVO) {
        return dynamicService.createDynamic(dynamicVO);
    }

    /**
     * 更新动态（后台管理系统）
     * @param dynamicId 动态ID
     * @return 返回更新结果
     */
    @PutMapping("/dynamic/update/{dynamicId}")
    public ResponseResult updateAdminDynamic(@PathVariable Integer dynamicId) {
        return dynamicService.updateGetDynamic(dynamicId);
    }

    /**
     * 删除动态（后台管理系统）
     * @param dynamicId 动态ID
     * @return 返回删除结果
     */
    @DeleteMapping("/dynamic/delete/{dynamicId}")
    public ResponseResult deleteAdminDynamic(@PathVariable Integer dynamicId) {
        return dynamicService.deleteDynamic(dynamicId);
    }
}
