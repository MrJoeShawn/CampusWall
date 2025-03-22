package com.campus.framework.dao.vo;

import lombok.Data;

import java.util.Date;

@Data
public class UnreadMessageVO {
    private Integer id;               // 消息 ID
    private String conversationId;    // 会话 ID
    private Integer senderId;         // 发送者 ID
    private Long receiverId;          // 接收者 ID
    private String messageType;       // 消息类型
    private String content;           // 消息内容
    private Integer status;           // 消息状态
    private Date sentTime;            // 发送时间
    private String senderName;        // 发送者用户名
    private String senderAvatar;      // 发送者头像
}
