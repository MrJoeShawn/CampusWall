package com.campus.wall.web.controller;

import com.campus.framework.dao.repository.ResponseResult;
import com.campus.framework.service.FavoritesService;
import com.campus.framework.service.LikesService;
import com.campus.framework.untils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DynamicInteractionController {

    @Autowired
    LikesService likesService;

    @Autowired
    FavoritesService favoritesService;

    /**
     * 用户点击动态喜欢
     * @param dynamicId
     * @return
     */
    @PostMapping("/like")
    public ResponseResult likeDynamic(Long dynamicId){
        Long userId = Long.valueOf(SecurityUtils.getUserId());
        return likesService.likeDynamic(userId,dynamicId);
    }

    /**
     * 用户点击动态收藏
     * @param dynamicId
     * @return
     */
    @PostMapping("/collect")
    public ResponseResult collectDynamic(Long dynamicId){
        Long userId = Long.valueOf(SecurityUtils.getUserId());
        return favoritesService.collectDynamic(userId,dynamicId);
    }

    /**
     * 个人主页展示用户喜欢的动态  我的喜欢
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/like")
    public ResponseResult getLikeStatus(Integer pageNum, Integer pageSize){
        Integer userId = SecurityUtils.getUserId();
        return likesService.getLikeStatus(pageNum,pageSize,userId);
    }

    /**
     * 个人主页展示用户收藏的动态 我的收藏
     * @param pageNum
     * @param pageSize
     * @return
     */
    @GetMapping("/collect")
    public ResponseResult getCollectStatus(Integer pageNum, Integer pageSize){
        Integer userId = SecurityUtils.getUserId();
        return favoritesService.getCollectStatus(pageNum,pageSize,userId);
    }
}
