package com.example.Compaytest.service;

import com.example.Compaytest.entity.BlackList;
import com.example.Compaytest.repository.BlackListRepo;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
@Service
public class BlackListService {
    private final BlackListRepo blacklistRepository;

    public BlackListService(BlackListRepo blacklistRepository) {
        this.blacklistRepository = blacklistRepository;
    }

     public List<BlackList> getAll() {
        return List.of();
    }

     public Optional<BlackList> getById(Integer id) {
        return Optional.empty();
    }

     public BlackList save(BlackList thanhToan) {
        return null;
    }

    public void save(String token, long millisLeft) {
        LocalDateTime expiredAt = LocalDateTime.now().plus(Duration.ofMillis(millisLeft));
        BlackList b = new BlackList();
        b.setTokenBlacklist(token);
        b.setExpiredAt(expiredAt);
        blacklistRepository.save(b);
    }

    public boolean isBlacklisted(String token) {
        return blacklistRepository.existsByTokenBlacklist(token);
    }



}
