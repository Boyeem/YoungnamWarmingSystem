package com.example.demo.controller;


import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AdminLoginController {

    private static final String ADMIN_ID = "admin";
    private static final String ADMIN_PASSWORD = "1234";

    // 로그인 폼 보여주기
    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    // 로그인 처리
    @PostMapping("/login")
    public String login(@RequestParam String id,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        if (ADMIN_ID.equals(id) && ADMIN_PASSWORD.equals(password)) {
            session.setAttribute("loggedIn", true);  // 세션에 로그인 상태 저장
            return "redirect:/admin";  // 로그인 성공 시 /disaster로 이동
        } else {
            model.addAttribute("message", "로그인 실패: 아이디 또는 비밀번호가 틀렸습니다.");
            return "login";
        }
    }

    // 로그아웃 기능 (선택)
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate(); // 세션 초기화
        return "redirect:/login";
    }
}
