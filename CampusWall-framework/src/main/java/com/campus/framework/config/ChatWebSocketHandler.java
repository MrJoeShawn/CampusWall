package com.campus.framework.config;

import com.campus.framework.dao.entity.ChatMessage;
import com.campus.framework.untils.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ChatWebSocketHandler extends TextWebSocketHandler {

    // 线程安全的 WebSocket 会话存储
    private static final Map<Long, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        Long userId = extractUserIdFromSession(session);

        if (userId == null) {
            session.close(CloseStatus.NOT_ACCEPTABLE);
            return;
        }

        sessions.put(userId, session);
        session.getAttributes().put("userId", userId);
        System.out.println("用户 " + userId + " 连接成功，当前在线用户：" + sessions.keySet());
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        Long senderId = getUserIdFromSession(session);
        if (senderId == null) {
            sendErrorMessage(session, "用户未认证");
            return;
        }

        try {
            ChatMessage chatMessage = objectMapper.readValue(message.getPayload(), ChatMessage.class);
            chatMessage.setSenderId(Math.toIntExact(senderId));

            Long receiverId = chatMessage.getReceiverId();
            if (receiverId == null) {
                sendErrorMessage(session, "接收者 ID 不能为空");
                return;
            }

            System.out.println("收到消息：" + chatMessage.getContent() + "，发送者：" + senderId + "，接收者：" + receiverId);

            sendMessageToUser(receiverId, chatMessage);
        } catch (Exception e) {
            sendErrorMessage(session, "消息解析异常: " + e.getMessage());
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Long userId = getUserIdFromSession(session);
        if (userId != null) {
            sessions.computeIfPresent(userId, (key, value) -> {
                try {
                    value.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            });
            System.out.println("用户 " + userId + " 断开连接，当前在线用户：" + sessions.keySet());
        }
    }

    private Long extractUserIdFromSession(WebSocketSession session) {
        String query = session.getUri().getQuery();
        String token = (query != null) ? extractToken(query) : null;

        if (token == null) {
            System.out.println("WebSocket 连接失败：缺少 token");
            return null;
        }

        try {
            Claims claims = JwtUtil.parseJWT(token);
            return (claims != null && claims.getSubject() != null) ? Long.parseLong(claims.getSubject()) : null;
        } catch (Exception e) {
            System.out.println("WebSocket 连接失败：JWT 解析错误 - " + e.getMessage());
            return null;
        }
    }

    private String extractToken(String query) {
        return query.startsWith("token=") ? query.substring(6) : null;
    }

    private Long getUserIdFromSession(WebSocketSession session) {
        return (Long) session.getAttributes().get("userId");
    }

    private void sendMessageToUser(Long receiverId, ChatMessage chatMessage) {
        WebSocketSession receiverSession = sessions.get(receiverId);
        if (receiverSession != null && receiverSession.isOpen()) {
            try {
                receiverSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatMessage)));
                System.out.println("消息已发送给用户 " + receiverId);
            } catch (IOException e) {
                System.out.println("消息发送失败：" + e.getMessage());
            }
        } else {
            System.out.println("用户 " + receiverId + " 不在线或连接已关闭");
        }
    }

    private void sendErrorMessage(WebSocketSession session, String errorMessage) {
        try {
            session.sendMessage(new TextMessage("{\"error\":\"" + errorMessage + "\"}"));
        } catch (IOException e) {
            System.out.println("发送错误消息失败：" + e.getMessage());
        }
    }
}
