package com.campus.framework.service;

import com.campus.framework.dao.entity.ChatMessage;

import java.util.List;

public interface ChatMessageService {

    void saveChatMessage(ChatMessage chatMessage);

    List<ChatMessage> getChatHistory(Long userId, Long friendId);
}

