package com.example.Compaytest.repository;

import com.example.Compaytest.entity.BlackList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface BlackListRepo extends JpaRepository<BlackList, Long> {
    boolean existsByTokenBlacklist(String token);
    Optional<BlackList> findByTokenBlacklist(String tokenBlacklist);

}
