package com.campus.framework.service;

import com.campus.framework.dao.entity.ChatMessage;
import com.campus.framework.dao.vo.UnreadMessageVO;

import java.util.List;

public interface ChatMessageService {

    void saveChatMessage(ChatMessage chatMessage);

    List<ChatMessage> getChatHistory(Long userId, Long friendId);

    int getUnreadMessageCount(Long aLong, Long friendId);

    void markMessagesAsRead(Long aLong, Long friendId);

    // 修改返回值类型，改为返回包含发送者信息的 VO
    List<UnreadMessageVO> getUnreadMessages(Long userId);

}

