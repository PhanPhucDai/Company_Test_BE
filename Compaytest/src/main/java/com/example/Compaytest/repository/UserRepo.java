package com.example.Compaytest.repository;

import com.example.Compaytest.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepo extends JpaRepository<User, Long> {

    // Tìm theo username
    Optional<User> findByUsername(String username);

    // Tìm theo email
    Optional<User> findByEmail(String email);

    // Kiểm tra tồn tại username
    boolean existsByUsername(String username);

    // Kiểm tra tồn tại email
    boolean existsByEmail(String email);
}
