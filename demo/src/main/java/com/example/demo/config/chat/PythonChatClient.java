package com.example.demo.config.chat;

import jakarta.annotation.PostConstruct;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
public class PythonChatClient extends WebSocketClient {

    private static final Logger log = LoggerFactory.getLogger(PythonChatClient.class);
    private static PythonChatClient instance;

    public PythonChatClient() throws Exception {
        super(new URI("ws://localhost:9000")); // Python 서버 주소
        instance = this;
    }

    @PostConstruct
    public void connectToServer() {
        this.connect();

        // 💡 연결 완료 후 주기적 메시지 송신 쓰레드 실행
        new Thread(() -> {
            try {
                // 연결될 때까지 대기
                while (!this.isOpen()) {
                    Thread.sleep(100);
                }

                log.info("💬 메시지 송신 쓰레드 시작됨");

                // 연결 후 10초마다 메시지 전송
                while (true) {
                    Thread.sleep(10000); // 10초 간격
                    sendToPython("✅ 자동 송신 메시지 from Spring");
                }

            } catch (InterruptedException e) {
                log.error("🧵 송신 쓰레드 중단됨", e);
                Thread.currentThread().interrupt();
            }
        }).start();
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        log.info("✅ Python WebSocket 서버에 연결되었습니다.");
    }

    @Override
    public void onMessage(String message) {
        log.info("💬 받은 메시지: {}", message);
        // ➕ 여기에 Thymeleaf나 DB 저장 로직 추가 가능
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        log.warn("⛔ 연결 종료: 코드={}, 이유={}, 원격={}", code, reason, remote);
    }

    @Override
    public void onError(Exception ex) {
        log.error("🚨 WebSocket 오류 발생: ", ex);
    }

    public static void sendToPython(String msg) {
        if (instance != null && instance.isOpen()) {
            instance.send(msg);
        } else {
            log.warn("⚠️ Python 서버에 연결되지 않았습니다. 메시지 전송 실패: {}", msg);
        }
    }
}
