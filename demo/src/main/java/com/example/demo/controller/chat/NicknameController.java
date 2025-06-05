package com.example.demo.controller.chat;

import com.example.demo.service.chat.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class NicknameController {

    private final UserService userService;

    @GetMapping("/api/assign-username")
    public ResponseEntity<String> assignUsername() {
        String username = userService.getAvailableNickname();
        return ResponseEntity.ok(username);
    }
}

