package com.campus.framework.config;

import com.campus.framework.dao.entity.ChatMessage;
import com.campus.framework.service.ChatMessageService;
import com.campus.framework.untils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    @Autowired
    private ChatMessageService chatMessageService;

    // 存储所有活跃的 WebSocket 会话，key 为用户 ID，value 为对应的 WebSocketSession
    private static final Map<Long, WebSocketSession> sessions = new ConcurrentHashMap<>();

    /**
     * 当 WebSocket 连接建立成功时调用该方法
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 解析 URI 中的查询参数获取 token
        String query = session.getUri().getQuery();
        String token = null;
        if (query != null) {
            for (String param : query.split("&")) {
                if (param.startsWith("token=")) {
                    token = param.substring(6); // 提取 token 部分
                    break;
                }
            }
        }

        // 如果没有 token，拒绝连接并关闭 WebSocket 会话
        if (token == null || token.isEmpty()) {
            session.close(CloseStatus.NOT_ACCEPTABLE);
            return;
        }

        // 解析 JWT token 获取用户 ID
        Claims claims = JwtUtil.parseJWT(token);
        Long userId = Long.parseLong(claims.getSubject());
        session.getAttributes().put("userId", userId); // 存入会话属性

        // 记录用户的 WebSocket 会话
        sessions.put(userId, session);
        System.out.println("用户 " + userId + " 连接成功");
    }

    /**
     * 处理接收到的文本消息
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        // 解析 JSON 消息
        String payload = message.getPayload();
        ObjectMapper objectMapper = new ObjectMapper();
        ChatMessage chatMessage = objectMapper.readValue(payload, ChatMessage.class);

        // 获取当前用户 ID
        Long senderId = getUserIdFromSession(session);
        chatMessage.setSenderId(senderId); // 确保发送者是当前用户

        // 生成会话 ID
        String conversationId = senderId < chatMessage.getReceiverId() ?
                senderId + "_" + chatMessage.getReceiverId() :
                chatMessage.getReceiverId() + "_" + senderId;
        chatMessage.setConversationId(conversationId);
        chatMessage.setSentTime(new Date());

        // **存储到数据库**
        chatMessageService.saveMessage(chatMessage);

        // **发送给接收者**
        WebSocketSession receiverSession = sessions.get(chatMessage.getReceiverId());
        if (receiverSession != null && receiverSession.isOpen()) {
            receiverSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatMessage)));
        }
    }

    /**
     * 处理获取聊天记录的请求
     */
    public void sendChatHistory(WebSocketSession session, Long receiverId) throws Exception {
        Long senderId = getUserIdFromSession(session);

        // 生成会话 ID
        String conversationId = senderId < receiverId ?
                senderId + "_" + receiverId :
                receiverId + "_" + senderId;

        // 获取聊天记录
        List<ChatMessage> chatHistory = chatMessageService.getChatHistory(conversationId);

        // 发送聊天记录
        ObjectMapper objectMapper = new ObjectMapper();
        session.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatHistory)));
    }

    /**
     * 当 WebSocket 连接关闭时调用该方法
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Long userId = getUserIdFromSession(session);
        if (userId != null) {
            sessions.remove(userId); // 从 sessions 中移除该用户的 WebSocket 会话
            System.out.println("用户 " + userId + " 断开连接");
        }
    }

    /**
     * 获取 WebSocket 会话中的 userId
     */
    private Long getUserIdFromSession(WebSocketSession session) {
        return (Long) session.getAttributes().get("userId");
    }
}
