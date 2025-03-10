package com.campus.framework.service.impl;

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
    public void saveMessage(ChatMessage chatMessage) {
        chatMessage.setSentTime(new Date());  // 设置消息时间
        chatMessage.setStatus(0);  // 默认状态为 "sent"
        chatMessageMapper.insertMessage(chatMessage);
    }

    @Override
    public List<ChatMessage> getChatHistory(String conversationId) {
        return chatMessageMapper.getMessagesByConversation(conversationId);
    }
}


