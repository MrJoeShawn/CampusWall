package com.campus.wall.web.controller;

import com.campus.framework.dao.repository.ResponseResult;
import com.campus.framework.service.FavoritesService;
import com.campus.framework.service.LikesService;
import com.campus.framework.untils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DynamicInteractionController {

    @Autowired
    LikesService likesService;

    @Autowired
    FavoritesService favoritesService;

    @PostMapping("/like")
    public ResponseResult likeDynamic(Long dynamicId){
        Long userId = Long.valueOf(SecurityUtils.getUserId());
        return likesService.likeDynamic(userId,dynamicId);
    }

    @PostMapping("/collect")
    public ResponseResult collectDynamic(Long dynamicId){
        Long userId = Long.valueOf(SecurityUtils.getUserId());
        return favoritesService.collectDynamic(userId,dynamicId);
    }
}
