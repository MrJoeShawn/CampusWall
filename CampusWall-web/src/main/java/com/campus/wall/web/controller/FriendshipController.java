package com.campus.wall.web.controller;

import com.campus.framework.dao.entity.Friendship;
import com.campus.framework.dao.enums.AppHttpCodeEnum;
import com.campus.framework.dao.repository.ResponseResult;
import com.campus.framework.dao.vo.UserInfoVo;
import com.campus.framework.service.FriendshipService;
import com.campus.framework.untils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friendship")
public class FriendshipController {

    @Autowired
    private FriendshipService friendshipService;

    // 获取好友列表
    @GetMapping("/friends")
    public ResponseEntity<List<UserInfoVo>> getFriendsList() {
        Integer userId = SecurityUtils.getUserId(); // 获取当前登录用户 ID
        List<UserInfoVo> friends = friendshipService.getFriendListWithUserInfo(userId);
        return ResponseEntity.ok(friends);
    }

    // 发送好友请求
    @PostMapping("/add")
    public ResponseResult sendFriendRequest(@RequestParam Integer friendId) {
        Integer userId = SecurityUtils.getUserId();

        // 校验是否是自己添加自己为好友
        if (userId.equals(friendId)) {
            return ResponseResult.errorResult(AppHttpCodeEnum.FRIEND_REQUEST_SENT);
        }

        // 调用服务层发送好友请求
        friendshipService.sendFriendRequest(userId, friendId);

        return ResponseResult.okResult(AppHttpCodeEnum.FRIEND_REQUEST_SENT);
    }


    // 获取好友请求列表
    @GetMapping("/requests")
    public ResponseEntity<List<UserInfoVo>> getFriendRequests() {
        Integer userId = SecurityUtils.getUserId();
        List<UserInfoVo> requests = friendshipService.getPendingFriendRequests(userId);
        return ResponseEntity.ok(requests);
    }


    // 处理好友请求（接受/拒绝）
    @PostMapping("/handleRequest")
    public ResponseResult handleFriendRequest(@RequestParam Integer friendId, @RequestParam boolean accept) {
        Integer userId = SecurityUtils.getUserId();
        friendshipService.handleFriendRequest(userId, friendId, accept);
        return ResponseResult.okResult(accept ? "好友请求已接受" : "好友请求已拒绝");
    }

    // 删除好友
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteFriend(@RequestParam Integer friendId) {
        Integer userId = SecurityUtils.getUserId();
        friendshipService.deleteFriend(userId, friendId);
        return ResponseEntity.ok("好友已删除");
    }
}

