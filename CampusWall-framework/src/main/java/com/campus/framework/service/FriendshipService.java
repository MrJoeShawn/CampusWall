package com.campus.framework.service;

import com.campus.framework.dao.entity.Friendship;
import com.campus.framework.dao.vo.UserInfoVo;
import io.swagger.v3.oas.annotations.servers.Server;

import java.util.List;

public interface FriendshipService {
    List<UserInfoVo> getFriendListWithUserInfo(Integer userId);

    void sendFriendRequest(Integer userId, Integer friendId);

    void handleFriendRequest(Integer userId, Integer friendId, boolean accept);

    void deleteFriend(Integer userId, Integer friendId);

    List<UserInfoVo> getPendingFriendRequests(Integer userId);
}

