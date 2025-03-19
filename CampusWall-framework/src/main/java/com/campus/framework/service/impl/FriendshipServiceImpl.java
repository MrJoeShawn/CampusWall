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
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
@Service
public class FriendshipServiceImpl extends ServiceImpl<FriendshipMapper, Friendship> implements FriendshipService {

    @Autowired
    private FriendshipMapper friendshipMapper;

    @Autowired
    private UsersMapper userMapper;

    // 获取用户的好友列表，包括用户信息
    @Override
    public List<UserInfoVo> getFriendListWithUserInfo(Integer userId) {
        // 查询指定用户的所有已接受的好友关系
        List<Friendship> friendships = friendshipMapper.selectList(
                new QueryWrapper<Friendship>()
                        .eq("user_id", userId)
                        .eq("status", "ACCEPTED") // 只查询已接受的好友
        );

        // 提取所有好友的ID
        List<Integer> friendIds = friendships.stream()
                .map(Friendship::getFriendId)
                .collect(Collectors.toList());

        // 如果没有好友，则返回空列表
        if (friendIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 根据好友ID列表查询用户信息，并转换为UserInfoVo对象
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
        // 检查用户是否尝试添加自己为好友
        if (userId.equals(friendId)) {
            throw new RuntimeException("不能添加自己为好友");
        }

        // 检查数据库中是否已存在该用户与好友的关系
        Friendship existingFriendship = friendshipMapper.selectOne(
                new QueryWrapper<Friendship>()
                        .eq("user_id", userId)
                        .eq("friend_id", friendId)
        );

        // 如果存在，抛出异常，防止重复发送好友请求或添加已为好友的用户
        if (existingFriendship != null) {
            throw new RuntimeException("好友请求已发送或已是好友");
        }

        // 创建新的好友关系对象
        Friendship friendship = new Friendship();
        friendship.setUserId(userId);
        friendship.setFriendId(friendId);
        friendship.setStatus("PENDING"); // 将状态设置为待接受
        friendship.setCreatedAt(new Date()); // 设置创建时间

        // 向数据库中插入新的好友关系记录
        friendshipMapper.insert(friendship);
    }

    // 处理好友请求
    @Override
    public void handleFriendRequest(Integer userId, Integer friendId, boolean accept) {
        // 查询是否有待处理的好友请求
        Friendship friendship = friendshipMapper.selectOne(
                new QueryWrapper<Friendship>()
                        .eq("user_id", friendId)
                        .eq("friend_id", userId)
                        .eq("status", "PENDING")
        );

        // 如果没有找到好友请求，则抛出异常
        if (friendship == null) {
            throw new RuntimeException("没有找到好友请求");
        }

        // 如果接受好友请求
        if (accept) {
            // 更新当前好友关系状态为已接受
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
            // 如果拒绝好友请求，直接删除请求
            friendshipMapper.deleteById(friendship.getId());
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

    // 获取所有待处理的好友请求
    @Override
    public List<UserInfoVo> getPendingFriendRequests(Integer userId) {
        List<Friendship> pendingRequests = friendshipMapper.selectList(
                new QueryWrapper<Friendship>()
                        .eq("friend_id", userId) // 该用户是被申请的一方
                        .eq("status", "PENDING") // 只查询待处理状态的请求
        );

        // 提取所有发送好友请求的用户 ID
        List<Integer> senderIds = pendingRequests.stream()
                .map(Friendship::getUserId) // 这里是 user_id，表示发送请求的人
                .collect(Collectors.toList());

        if (senderIds.isEmpty()) {
            return new ArrayList<>();
        }

        // 查询用户信息并封装成 UserInfoVo
        return userMapper.selectBatchIds(senderIds).stream()
                .map(user -> new UserInfoVo()
                        .setId(user.getId())
                        .setUsername(user.getUsername())
                        .setFullName(user.getFullName())
                        .setProfilePictureUrl(user.getProfilePictureUrl()))
                .collect(Collectors.toList());
    }

}