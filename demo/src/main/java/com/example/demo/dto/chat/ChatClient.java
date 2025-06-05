package com.example.demo.dto.chat;

import jakarta.annotation.PostConstruct;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.stereotype.Component;

import java.net.URI;

@Component
public class ChatClient extends WebSocketClient {

    private static ChatClient instance;

    public ChatClient() throws Exception {
        super(new URI("ws://localhost:9000")); // Python WebSocket 서버 주소
        instance = this;
    }

    @PostConstruct
    public void start() {
        this.connect();
    }

    @Override
    public void onOpen(ServerHandshake handshake) {
        System.out.println("Python 서버에 연결됨.");
    }

    @Override
    public void onMessage(String message) {
        System.out.println("수신된 메시지: " + message);
        // TODO: 이후 프론트에 브로드캐스트 하려면 WebSocketSession 관리 추가
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        System.out.println("연결 종료됨: " + reason);
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
    }

    public static void sendToPython(String msg) {
        if (instance != null && instance.isOpen()) {
            instance.send(msg);
        }
    }
}
