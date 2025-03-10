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

    private static final Map<Long, WebSocketSession> sessions = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String query = session.getUri().getQuery();
        String token = (query != null && query.contains("token=")) ? query.split("token=")[1].split("&")[0] : null;

        if (token == null || token.isEmpty()) {
            session.close(CloseStatus.NOT_ACCEPTABLE);
            return;
        }

        Claims claims = JwtUtil.parseJWT(token);
        Long userId = Long.parseLong(claims.getSubject());
        session.getAttributes().put("userId", userId);
        sessions.put(userId, session);
        System.out.println("用户 " + userId + " 连接成功");
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        ChatMessage chatMessage = objectMapper.readValue(message.getPayload(), ChatMessage.class);

        Long senderId = getUserIdFromSession(session);
        chatMessage.setSenderId(senderId);

        String conversationId = senderId < chatMessage.getReceiverId() ?
                senderId + "_" + chatMessage.getReceiverId() :
                chatMessage.getReceiverId() + "_" + senderId;
        chatMessage.setConversationId(conversationId);
        chatMessage.setSentTime(new Date());

        WebSocketSession receiverSession = sessions.get(chatMessage.getReceiverId());
        if (receiverSession != null && receiverSession.isOpen()) {
            receiverSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatMessage)));
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Long userId = getUserIdFromSession(session);
        if (userId != null) {
            sessions.remove(userId);
            System.out.println("用户 " + userId + " 断开连接");
        }
    }

    private Long getUserIdFromSession(WebSocketSession session) {
        return (Long) session.getAttributes().get("userId");
    }
}
