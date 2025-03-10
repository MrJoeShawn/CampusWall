package com.campus.framework.dao.entity;
import lombok.Data;
import java.io.Serializable;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

/**
 * 聊天消息表(ChatMessage)表实体类
 *
 * @author makejava
 * @since 2025-03-09 19:25:19
 */
@SuppressWarnings("serial")
@Data
@AllArgsConstructor
@NoArgsConstructor
@TableName("chat_message")
public class ChatMessage  {
    //消息ID
    @TableId
    private Integer id;
    //会话ID    
    private String conversationId;
    //发送者ID    
    private Integer senderId;
    //接收者ID    
    private Long receiverId;
    //消息类型    
    private String messageType;
    //消息内容    
    private String content;
    //消息状态（0=sent, 1=delivered, 2=read）    
    private Integer status;
    //发送时间    
    private Date sentTime;
    //阅读时间    
    private Date readTime;
}
