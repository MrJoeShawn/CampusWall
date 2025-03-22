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

    // sessions 字段保持 private，防止外部直接访问
    public static final Map<Long, WebSocketSession> sessions = new ConcurrentHashMap<>();

    /**
     * 在 WebSocket 连接建立后执行的操作
     *
     * @param session WebSocket 会话对象，包含会话的相关信息和操作方法
     * @throws Exception 如果在处理过程中遇到错误，抛出异常
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        // 从会话的 URI 中获取查询字符串
        String query = session.getUri().getQuery();
        // 提取查询字符串中的 token 参数值
        String token = (query != null && query.contains("token=")) ? query.split("token=")[1].split("&")[0] : null;

        // 如果 token 为空或为空字符串，则关闭会话
        if (token == null || token.isEmpty()) {
            session.close(CloseStatus.NOT_ACCEPTABLE);
            return;
        }

        // 解析 JWT token 以获取用户信息
        Claims claims = JwtUtil.parseJWT(token);
        // 从 JWT 的 claims 中获取用户 ID
        Long userId = Long.parseLong(claims.getSubject());
        // 将用户 ID 与 WebSocket 会话关联
        session.getAttributes().put("userId", userId);
        // 将用户 ID 和对应的 WebSocket 会话存储到 sessions 中
        sessions.put(userId, session);
        // 打印用户连接成功的日志信息
        System.out.println("用户 " + userId + " 连接成功");
    }

    /**
     * 处理接收到的文本消息
     *
     * @param session  WebSocket 会话，代表与客户端的连接
     * @param message  接收到的文本消息
     * @throws Exception 如果消息处理过程中发生错误
     */
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        ChatMessage chatMessage = objectMapper.readValue(message.getPayload(), ChatMessage.class);

        Long senderId = getUserIdFromSession(session);
        chatMessage.setSenderId(Math.toIntExact(senderId));

        String conversationId = senderId < chatMessage.getReceiverId() ?
                senderId + "_" + chatMessage.getReceiverId() :
                chatMessage.getReceiverId() + "_" + senderId;
        chatMessage.setConversationId(conversationId);
        chatMessage.setSentTime(new Date());

        // 保存消息
        chatMessageService.saveChatMessage(chatMessage);

        // 如果接收者在线，发送消息并标记为已读
        WebSocketSession receiverSession = getSession(chatMessage.getReceiverId()); // 通过 getSession 访问 sessions
        if (receiverSession != null && receiverSession.isOpen()) {
            receiverSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(chatMessage)));

            // 标记接收到的消息为已读
            chatMessageService.markMessagesAsRead(chatMessage.getReceiverId(), Long.valueOf(chatMessage.getSenderId()));

            // 通知发送者消息已读
            ChatMessage readReceipt = new ChatMessage();
            readReceipt.setSenderId(Math.toIntExact(chatMessage.getReceiverId()));
            readReceipt.setReceiverId(Long.valueOf(chatMessage.getSenderId()));
            readReceipt.setConversationId(conversationId);
            readReceipt.setStatus(2); // 已读
            receiverSession.sendMessage(new TextMessage(objectMapper.writeValueAsString(readReceipt)));
        }
    }

    /**
     * 连接关闭时的处理
     *
     * @param session WebSocket 会话对象
     * @param status  连接关闭状态
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        Long userId = getUserIdFromSession(session);
        if (userId != null) {
            sessions.remove(userId);
            System.out.println("用户 " + userId + " 断开连接");
        }
    }

    /**
     * 获取当前 WebSocket 会话中的用户 ID
     *
     * @param session WebSocket 会话对象
     * @return 用户 ID
     */
    private Long getUserIdFromSession(WebSocketSession session) {
        return (Long) session.getAttributes().get("userId");
    }

    /**
     * 获取指定用户 ID 对应的 WebSocket 会话
     *
     * @param userId 用户 ID
     * @return WebSocket 会话对象
     */
    public static WebSocketSession getSession(Long userId) {
        return sessions.get(userId); // 通过 getSession 方法访问 sessions
    }
}
