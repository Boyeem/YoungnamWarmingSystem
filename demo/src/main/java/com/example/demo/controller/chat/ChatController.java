package com.example.demo.controller.chat;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ChatController {

    @GetMapping("/home")
    public String assignedPage() {
        return "home";
    }
}
