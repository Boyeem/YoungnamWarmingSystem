package com.example.demo.config.chat;


import com.example.demo.service.chat.UserService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class NicknameResetScheduler {

    private final UserService userService;

    // 1시간마다 실행 (3600000ms)
    @Scheduled(fixedRate = 60 * 60 * 1000)
    public void resetExpiredNicknames() {
        System.out.println("⏰ 만료된 닉네임 초기화 중...");
        userService.resetExpiredNicknames();
    }
}
