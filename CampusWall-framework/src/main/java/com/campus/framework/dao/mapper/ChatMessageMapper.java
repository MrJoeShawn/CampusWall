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
public interface ChatMessageMapper extends BaseMapper<ChatMessage> {
}




