package com.example.demo.controller.disaster;

import jakarta.websocket.server.ServerEndpoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@ServerEndpoint("/ws/disaster")
@Component
public class DisasterWebSocket {

    private static SimpMessagingTemplate messagingTemplate;

    @Autowired
    public void setMessagingTemplate(SimpMessagingTemplate template) {
        DisasterWebSocket.messagingTemplate = template;
    }

}
