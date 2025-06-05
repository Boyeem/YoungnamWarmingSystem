package com.example.demo.service.chat;


import com.example.demo.repository.chat.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    @Transactional
    public String getAvailableNickname() {
        return userRepository.findAvailableNickname()
                .map(user -> {
                    userRepository.assignNickname(user.getUsername());
                    return user.getUsername();
                })
                .orElse("NONE_AVAILABLE");  // 닉네임 없을 때
    }



    @Transactional
    public void resetExpiredNicknames() {
        userRepository.resetExpiredNicknames();
    }
}
