package com.campus.framework.service;

import com.campus.framework.dao.entity.ChatMessage;

import java.util.List;

public interface ChatMessageService {
    void saveMessage(ChatMessage chatMessage);
    List<ChatMessage> getChatHistory(String conversationId);
}

