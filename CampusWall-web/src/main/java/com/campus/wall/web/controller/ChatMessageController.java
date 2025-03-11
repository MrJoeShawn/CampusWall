package com.campus.wall.web.controller;

import com.campus.framework.dao.entity.ChatMessage;
import com.campus.framework.service.ChatMessageService;
import com.campus.framework.untils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/chat")
public class ChatMessageController {

    @Autowired
    private ChatMessageService chatMessageService;

    // 查询与好友的聊天记录
    @GetMapping("/history")
    public List<ChatMessage> getChatHistory(@RequestParam("friendId") Long friendId) {
        Integer userId = SecurityUtils.getUserId();  // 获取当前登录用户ID
        return chatMessageService.getChatHistory(Long.valueOf(userId), friendId);
    }

    // 保存聊天记录
    @PostMapping("/send")
    public ResponseEntity<Void> sendMessage(@RequestBody ChatMessage chatMessage) {
        Integer userId = SecurityUtils.getUserId();  // 获取当前登录用户ID
        chatMessage.setSenderId(userId);  // 设置当前用户为发送者
        chatMessage.setSentTime(new Date());  // 设置当前时间为发送时间
        chatMessageService.saveChatMessage(chatMessage);
        return ResponseEntity.ok().build();
    }
}