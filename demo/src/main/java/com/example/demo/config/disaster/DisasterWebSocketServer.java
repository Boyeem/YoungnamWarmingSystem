package com.example.demo.config.disaster;

import jakarta.websocket.*;
import jakarta.websocket.server.ServerEndpoint;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@ServerEndpoint("/ws/disaster/python")
@Component
public class DisasterWebSocketServer {

    private static Session session;

    private static final Set<Session> sessions = ConcurrentHashMap.newKeySet();
    private static final ExecutorService executor = Executors.newCachedThreadPool(); // 동적 스레드 풀
    private static final ObjectMapper objectMapper = new ObjectMapper(); // JSON 파싱을 위한 ObjectMapper

    @OnOpen
    public void onOpen(Session session) {
        sessions.add(session);
        System.out.println("✅ 연결됨: " + session.getId());
    }

    public static void sendPauseCommand() {
        if (sessions.isEmpty()) {
            System.out.println("⚠ 연결된 클라이언트가 없습니다.");
            return;
        }
         for (Session s : sessions) {
        executor.submit(() -> {
            if (s.isOpen()) {
                try {
                    s.getBasicRemote().sendText("{\"status\": \"pause\"}");
                    System.out.println("⏸️ Python 서버에 멈춤 명령 전송 (session: " + s.getId() + ")");
                } catch (IOException e) {
                    System.err.println("❌ 일시정지 명령 전송 실패 (session: " + s.getId() + "): " + e.getMessage());
                }
            }
        });
    }
    }

 

    @OnMessage
    public void onMessage(String message, Session session) {
        System.out.println("🔥 수신된 메시지: " + message);

        try {
            JsonNode jsonNode = objectMapper.readTree(message);

            // Process start_time and end_time
            if (jsonNode.has("start_time")) {
                double startTime = jsonNode.get("start_time").asDouble();
                long endTime = System.currentTimeMillis() / 1000; // Convert to seconds
                long processingTime = endTime - (long) startTime;

                System.out.println("처리 시간: " + processingTime);

                // Return the processing time to the client
                JsonNode response = objectMapper.createObjectNode()
                        .put("status", "success")
                        .put("processing_time", processingTime);

                // Send the processing time back to the client
                for (Session s : sessions) {
                    if (s.isOpen()) {
                        s.getBasicRemote().sendText(response.toString()); // Send processing time to client
                    }
                }
            }

            // Handle other parts of the message (e.g., "messages", etc.)
            if (jsonNode.has("messages") && jsonNode.get("messages").isArray()) {
                // Process the disaster messages
                for (JsonNode msg : jsonNode.get("messages")) {
                    String type = msg.get("type").asText();
                    String agency = msg.get("agency").asText();
                    String distance = msg.get("distance").asText();
                    String content = msg.get("content").asText();
                    String essentialItems = msg.has("essential_items") ? msg.get("essential_items").asText() : "없음";

                    System.out.printf("[%s] %s (거리: %s)\n   🧰 필수생존품: %s\n", type, agency, distance, essentialItems);
                }
            } else {
                System.out.println("⚠ messages 필드가 없거나 비어 있습니다.");
            }

        } catch (Exception e) {
            System.err.println("❌ 메시지 파싱 오류: " + e.getMessage());
        }

        // Broadcast: Send the received message to all connected clients
        for (Session s : sessions) {
            executor.submit(() -> {
                if (s.isOpen()) {
                    try {
                        s.getBasicRemote().sendText(message); // Send message to all clients
                    } catch (IOException e) {
                        System.err.println("❌ 전송 실패: " + e.getMessage());
                    }
                }
            });
        }
    }





    @OnClose
    public void onClose(Session session) {
        sessions.remove(session);
        System.out.println("❌ 연결 종료: " + session.getId());
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.err.println("❗ 오류: " + throwable.getMessage());
    }
}
