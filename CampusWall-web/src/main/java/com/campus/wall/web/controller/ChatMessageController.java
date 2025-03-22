package com.campus.wall.web.controller;

import com.campus.framework.dao.entity.ChatMessage;
import com.campus.framework.dao.vo.UnreadMessageVO;
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

    // 查询未读消息数量
    @GetMapping("/unreadCount")
    public ResponseEntity<Integer> getUnreadCount(@RequestParam Long friendId) {
        Integer userId = SecurityUtils.getUserId();
        int unreadCount = chatMessageService.getUnreadMessageCount(Long.valueOf(userId), friendId);
        return ResponseEntity.ok(unreadCount);
    }


    // 标记为已读
    @PostMapping("/markAsRead")
    public ResponseEntity<Void> markMessagesAsRead(@RequestParam Long friendId) {
        Integer userId = SecurityUtils.getUserId();
        chatMessageService.markMessagesAsRead(Long.valueOf(userId), friendId);
        return ResponseEntity.ok().build();
    }

    // 获取未读消息列表
    @GetMapping("/unreadMessages")
    public ResponseEntity<List<UnreadMessageVO>> getUnreadMessages() {
        Integer userId = SecurityUtils.getUserId();  // 获取当前登录用户 ID
        List<UnreadMessageVO> unreadMessages = chatMessageService.getUnreadMessages(Long.valueOf(userId));
        return ResponseEntity.ok(unreadMessages);
    }



    // 保存聊天记录
    @PostMapping("/send")
    public ResponseEntity<Void> sendMessage(@RequestBody ChatMessage chatMessage) {
        Integer userId = SecurityUtils.getUserId();  // 获取当前登录用户ID
//        chatMessage.setSenderId(Long.valueOf(userId));  // 设置当前用户为发送者
//        chatMessage.setSentTime(new Date());  // 设置当前时间为发送时间
//        chatMessageService.saveChatMessage(chatMessage);
        return ResponseEntity.ok().build();
    }
}