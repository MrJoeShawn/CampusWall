package com.campus.framework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.campus.framework.config.ChatWebSocketHandler;
import com.campus.framework.dao.entity.ChatMessage;
import com.campus.framework.dao.entity.Users;
import com.campus.framework.dao.mapper.ChatMessageMapper;
import com.campus.framework.dao.mapper.UsersMapper;
import com.campus.framework.dao.vo.UnreadMessageVO;
import com.campus.framework.service.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Service
public class ChatMessageServiceImpl implements ChatMessageService {

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @Autowired
    private UsersMapper usersMapper;


    /**
     * 保存聊天记录
     * @param chatMessage
     */
    @Override
    public void saveChatMessage(ChatMessage chatMessage) {
        Integer senderId = chatMessage.getSenderId();
        Long receiverId = chatMessage.getReceiverId();

        // 构造会话ID
        String conversationId = senderId < receiverId ?
                senderId + "_" + receiverId :
                receiverId + "_" + senderId;

        chatMessage.setConversationId(conversationId); // 设置会话ID

        // 判断接收者是否在线
        WebSocketSession receiverSession = ChatWebSocketHandler.sessions.get(receiverId);
        if (receiverSession != null && receiverSession.isOpen()) {
            chatMessage.setStatus(2);  // 在线时设置为已读
        } else {
            chatMessage.setStatus(1);  // 离线时设置为未读
        }

        // 保存聊天记录到数据库
        chatMessageMapper.insert(chatMessage);
    }


    // 根据用户ID和好友ID获取聊天记录
    @Override
    public List<ChatMessage> getChatHistory(Long userId, Long friendId) {
        // 构造会话ID
        String conversationId = userId < friendId ?
                userId + "_" + friendId :
                friendId + "_" + userId;

        // 查询聊天记录
        return chatMessageMapper.selectList(new QueryWrapper<ChatMessage>()
                .eq("conversation_id", conversationId)
                .orderByAsc("sent_time"));
    }

    /**
     * 获取未读消息数量
     * @param userId
     * @param friendId
     * @return
     */
    public int getUnreadMessageCount(Long userId, Long friendId) {
        String conversationId = userId < friendId ?
                userId + "_" + friendId :
                friendId + "_" + userId;

        return chatMessageMapper.selectCount(new QueryWrapper<ChatMessage>()
                .eq("conversation_id", conversationId)
                .eq("receiver_id", userId) // 只查询自己未读的消息
                .eq("status", 1)); // 只查询已送达但未读的消息
    }

    /**
     * 标记消息为已读
     * @param userId
     * @param friendId
     */
    public void markMessagesAsRead(Long userId, Long friendId) {
        String conversationId = userId < friendId ?
                userId + "_" + friendId :
                friendId + "_" + userId;

        chatMessageMapper.update(
                new ChatMessage(), // 更新的内容
                new UpdateWrapper<ChatMessage>()
                        .set("status", 2) // 已读
                        .set("read_time", new Date()) // 记录已读时间
                        .eq("conversation_id", conversationId)
                        .eq("receiver_id", userId)
                        .eq("status", 1) // 只更新未读消息
        );
    }


    @Override
    public List<UnreadMessageVO> getUnreadMessages(Long userId) {
        List<ChatMessage> messages = chatMessageMapper.selectList(new QueryWrapper<ChatMessage>()
                .eq("receiver_id", userId)  // 查询当前用户接收的消息
                .eq("status", 1)            // 1 表示未读状态
                .orderByAsc("sent_time"));  // 按时间排序

        // 创建 VO 列表
        List<UnreadMessageVO> result = new ArrayList<>();

        for (ChatMessage message : messages) {
            Long senderId = Long.valueOf(message.getSenderId());
            Users sender = usersMapper.selectById(senderId);  // 获取发送者信息

            // 组装 VO 对象
            UnreadMessageVO vo = new UnreadMessageVO();
            vo.setId(message.getId());
            vo.setConversationId(message.getConversationId());
            vo.setSenderId(message.getSenderId());
            vo.setReceiverId(message.getReceiverId());
            vo.setMessageType(message.getMessageType());
            vo.setContent(message.getContent());
            vo.setStatus(message.getStatus());
            vo.setSentTime(message.getSentTime());

            // 设置发送者信息
            vo.setSenderName(sender.getFullName());
            vo.setSenderAvatar(sender.getProfilePictureUrl());

            result.add(vo);
        }

        return result;
    }
}




