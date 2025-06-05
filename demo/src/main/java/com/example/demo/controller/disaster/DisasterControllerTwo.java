package com.example.demo.controller.disaster;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.config.disaster.DisasterWebSocketServer;

@RestController
public class DisasterControllerTwo {

    @PostMapping("/pause")
    public void handlePause() {
        // Java WebSocket 서버의 sendPauseCommand() 호출
        DisasterWebSocketServer.sendPauseCommand();
    }
}
