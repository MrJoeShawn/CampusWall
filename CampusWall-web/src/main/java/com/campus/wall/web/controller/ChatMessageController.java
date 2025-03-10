package com.campus.wall.web.controller;

import com.campus.framework.dao.entity.ChatMessage;
import com.campus.framework.service.ChatMessageService;
import com.campus.framework.untils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
public class ChatMessageController {

    @Autowired
    private ChatMessageService chatMessageService;

    /**
     * 获取与某个用户的聊天记录
     */
    @GetMapping("/history")
    public ResponseEntity<List<ChatMessage>> getChatHistory(@RequestParam Long receiverId) {
        Integer senderId = SecurityUtils.getUserId(); // 获取当前用户 ID

        // 生成会话 ID（两人 ID 组合，保证顺序一致）
        String conversationId = senderId < receiverId ? senderId + "_" + receiverId : receiverId + "_" + senderId;

        List<ChatMessage> chatHistory = chatMessageService.getChatHistory(conversationId);
        return ResponseEntity.ok(chatHistory);
    }

    /**
     * 存储用户发送的消息
     */
    @PostMapping("/sendMessage")
    public ResponseEntity<String> sendMessage(@RequestBody ChatMessage chatMessage) {
        Integer senderId = SecurityUtils.getUserId(); // 确保消息的发送者是当前用户
        chatMessage.setSenderId(Long.valueOf(senderId));

        // 生成会话 ID
        String conversationId = senderId < chatMessage.getReceiverId() ?
                senderId + "_" + chatMessage.getReceiverId() :
                chatMessage.getReceiverId() + "_" + senderId;
        chatMessage.setConversationId(conversationId);

        // 存储消息
        chatMessageService.saveMessage(chatMessage);

        return ResponseEntity.ok("消息存储成功");
    }
}



