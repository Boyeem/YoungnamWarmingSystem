package com.example.demo.repository.chat;

import com.example.demo.entity.chat.User;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface UserRepository extends CrudRepository<User, String> {



    @Query(value = "SELECT * FROM users WHERE assigned_at IS NULL OR assigned_at < NOW() - INTERVAL 1 DAY LIMIT 1", nativeQuery = true)
    Optional<User> findAvailableNickname();

    @Modifying
    @Transactional
    @Query(value = "UPDATE users SET assigned_at = NOW() WHERE username = :username", nativeQuery = true)
    void assignNickname(@org.springframework.data.repository.query.Param("username") String username);

    @Modifying
    @Transactional
    @Query(value = "UPDATE users SET assigned_at = NULL WHERE assigned_at < NOW() - INTERVAL 1 DAY", nativeQuery = true)
    void resetExpiredNicknames();
}
