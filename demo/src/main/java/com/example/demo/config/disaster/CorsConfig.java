package com.example.demo.config.disaster;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/pause")
                .allowedOrigins("http://localhost:3000") // 프론트엔드 도메인
                .allowedMethods("POST");
    }
}