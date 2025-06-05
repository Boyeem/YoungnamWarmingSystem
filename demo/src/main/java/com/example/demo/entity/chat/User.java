package com.example.demo.entity.chat;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
public class User {

    @Id
    private String username; // PK
    private LocalDateTime assignedAt; // 닉네임 할당 시간 (null이면 사용 가능)
}
