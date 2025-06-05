package com.example.demo.controller.disaster;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DisasterController {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @MessageMapping("/disaster/python")  // Flask가 보낼 때 사용
    public void receiveDisasterData(String payload) {
        messagingTemplate.convertAndSend("/topic/disaster", payload);
    }

    @GetMapping("/admin")
    public String showDisasterPage(HttpSession session, Model model) {
        Boolean loggedIn = (Boolean) session.getAttribute("loggedIn");

        if (loggedIn != null && loggedIn) {
            return "admin"; // 로그인한 경우에만 접근 가능
        } else {
            model.addAttribute("message", "접근하려면 로그인하세요.");
            return "login"; // 로그인 안 되어 있으면 로그인 페이지로 이동
        }
    }

}