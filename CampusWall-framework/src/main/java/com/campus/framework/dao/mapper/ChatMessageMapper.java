package com.campus.framework.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.campus.framework.dao.entity.ChatMessage;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 聊天消息表(ChatMessage)表数据库访问层
 *
 * @author makejava
 * @since 2025-03-09 19:25:19
 */
@Mapper
public interface ChatMessageMapper {

    @Insert("INSERT INTO chat_message (sender_id, receiver_id, content, sent_time, conversation_id, status) " +
            "VALUES (#{senderId}, #{receiverId}, #{content}, #{sentTime}, #{conversationId}, #{status})")
    void insertMessage(ChatMessage chatMessage);

    @Select("SELECT * FROM chat_message WHERE conversation_id = #{conversationId} ORDER BY sent_time ASC")
    List<ChatMessage> getMessagesByConversation(@Param("conversationId") String conversationId);
}


