package com.example.demo.handler.disaster;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class DisasterSocketHandler extends TextWebSocketHandler {

    private static final List<WebSocketSession> pythonSessions = new CopyOnWriteArrayList<>();
    private static final List<WebSocketSession> htmlSessions = new CopyOnWriteArrayList<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        if (session.getUri().toString().contains("python")) {
            pythonSessions.add(session);
            System.out.println("🐍 Python 연결: " + session.getId());
        } else {
            htmlSessions.add(session);
            System.out.println("🌐 HTML 연결: " + session.getId());
        }
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) {
        String payload = message.getPayload();
        System.out.println("📩 수신: " + payload);

        // Python에서 온 메시지를 HTML 클라이언트에게 전송
        if (pythonSessions.contains(session)) {
            htmlSessions.forEach(s -> {
                try {
                    if (s.isOpen()) {
                        s.sendMessage(message);
                        System.out.println("➡ HTML 전송 완료");
                    }
                } catch (Exception e) {
                    System.out.println("❌ HTML 전송 실패: " + e.getMessage());
                }
            });
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        if (pythonSessions.contains(session)) {
            pythonSessions.remove(session);
            System.out.println("🐍 Python 연결 종료");
        } else {
            htmlSessions.remove(session);
            System.out.println("🌐 HTML 연결 종료");
        }
    }
}