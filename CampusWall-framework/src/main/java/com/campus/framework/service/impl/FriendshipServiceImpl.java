package com.campus.framework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.campus.framework.dao.entity.Friendship;
import com.campus.framework.dao.mapper.FriendshipMapper;
import com.campus.framework.dao.mapper.UsersMapper;
import com.campus.framework.dao.vo.UserInfoVo;
import com.campus.framework.service.FriendshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class FriendshipServiceImpl extends ServiceImpl<FriendshipMapper, Friendship> implements FriendshipService {

    @Autowired
    private FriendshipMapper friendshipMapper;

    @Autowired
    private UsersMapper userMapper;

    // 获取好友列表
    @Override
    public List<UserInfoVo> getFriendListWithUserInfo(Integer userId) {
        List<Friendship> friendships = friendshipMapper.selectList(
                new QueryWrapper<Friendship>()
                        .eq("user_id", userId)
                        .eq("status", "ACCEPTED") // 只查询已接受的好友
        );

        List<Integer> friendIds = friendships.stream()
                .map(Friendship::getFriendId)
                .collect(Collectors.toList());

        if (friendIds.isEmpty()) {
            return new ArrayList<>();
        }

        return userMapper.selectBatchIds(friendIds).stream()
                .map(user -> new UserInfoVo()
                        .setId(user.getId())
                        .setUsername(user.getUsername())
                        .setFullName(user.getFullName())
                        .setProfilePictureUrl(user.getProfilePictureUrl()))
                .collect(Collectors.toList());
    }

    // 发送好友请求
    @Override
    public void sendFriendRequest(Integer userId, Integer friendId) {
        if (userId.equals(friendId)) {
            throw new RuntimeException("不能添加自己为好友");
        }

        Friendship existingFriendship = friendshipMapper.selectOne(
                new QueryWrapper<Friendship>()
                        .eq("user_id", userId)
                        .eq("friend_id", friendId)
        );

        if (existingFriendship != null) {
            throw new RuntimeException("好友请求已发送或已是好友");
        }

        Friendship friendship = new Friendship();
        friendship.setUserId(userId);
        friendship.setFriendId(friendId);
        friendship.setStatus("PENDING"); // 等待对方接受
        friendship.setCreatedAt(new Date());

        friendshipMapper.insert(friendship);
    }

    // 处理好友请求
    @Override
    public void handleFriendRequest(Integer userId, Integer friendId, boolean accept) {
        Friendship friendship = friendshipMapper.selectOne(
                new QueryWrapper<Friendship>()
                        .eq("user_id", friendId)
                        .eq("friend_id", userId)
                        .eq("status", "PENDING")
        );

        if (friendship == null) {
            throw new RuntimeException("没有找到好友请求");
        }

        if (accept) {
            friendship.setStatus("ACCEPTED");
            friendshipMapper.updateById(friendship);

            // 互相添加好友（对方也要存一条）
            Friendship reverseFriendship = new Friendship();
            reverseFriendship.setUserId(userId);
            reverseFriendship.setFriendId(friendId);
            reverseFriendship.setStatus("ACCEPTED");
            reverseFriendship.setCreatedAt(new Date());
            friendshipMapper.insert(reverseFriendship);
        } else {
            friendshipMapper.deleteById(friendship.getId()); // 直接删除请求
        }
    }

    // 删除好友
    @Override
    public void deleteFriend(Integer userId, Integer friendId) {
        friendshipMapper.delete(
                new QueryWrapper<Friendship>()
                        .eq("user_id", userId)
                        .eq("friend_id", friendId)
                        .eq("status", "ACCEPTED")
        );

        friendshipMapper.delete(
                new QueryWrapper<Friendship>()
                        .eq("user_id", friendId)
                        .eq("friend_id", userId)
                        .eq("status", "ACCEPTED")
        );
    }
}

