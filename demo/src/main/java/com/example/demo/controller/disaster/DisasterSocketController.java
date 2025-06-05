package com.example.demo.controller.disaster;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class DisasterSocketController {

    @MessageMapping("/python") // '/ws/disaster/python'으로 매핑
    @SendTo("/topic/disaster")
    public Map<String, Object> handlePythonMessage(@Payload Map<String, Object> payload) {
        System.out.println("Python에서 수신: " + payload);

        // 응답 메시지 생성
        Map<String, Object> response = new HashMap<>();
        response.put("status", "received");
        response.put("count", ((List<?>)payload.get("messages")).size());
        response.put("timestamp", System.currentTimeMillis());

        return response;
    }
}