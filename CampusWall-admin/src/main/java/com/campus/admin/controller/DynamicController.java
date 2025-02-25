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
}
