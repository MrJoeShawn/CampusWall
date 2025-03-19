package com.campus.framework.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.campus.framework.dao.entity.ChatMessage;
import com.campus.framework.dao.mapper.ChatMessageMapper;
import com.campus.framework.service.ChatMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ChatMessageServiceImpl implements ChatMessageService {

    @Autowired
    private ChatMessageMapper chatMessageMapper;

    @Override
    public void saveChatMessage(ChatMessage chatMessage) {
        // 获取当前用户ID和接收者ID，生成会话ID
        Integer senderId = chatMessage.getSenderId();
        Long receiverId = chatMessage.getReceiverId();

        // 构造会话ID
        String conversationId = senderId < receiverId ?
                senderId + "_" + receiverId :
                receiverId + "_" + senderId;

        chatMessage.setConversationId(conversationId); // 设置会话ID

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
}




