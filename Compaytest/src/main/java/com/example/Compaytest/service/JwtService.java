package com.example.Compaytest.service;

import com.example.Compaytest.entity.User;
import com.example.Compaytest.repository.UserRepo;
import com.example.Compaytest.service.UserService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;

@Service
public class JwtService {

    private final UserRepo userRepo;

     private String secret = "bpidaylachuoibaomatcuabanratdaicvsdycv2v8daylachuoibaomatcuabanratdai03bd0daylachuoibaomatcuabanratdai2bc808VEdcs123111";

    public JwtService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    // Tạo token
    public String generateToken(String username) {
        Optional<User> user = userRepo.findByUsername(username);
        Map<String, Object> claims = new HashMap<>();
        claims.put("id", user.get().getId());
        claims.put("username", user.get().getUsername());
        claims.put("roles", List.of("ROLE_USER"));

        return createToken(claims, user.get().getUsername());
    }

    private String createToken(Map<String, Object> claims, String subject) {
        long expirationTime = 1000 * 60 * 60 * 1; // 1 giờ
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    // Lấy Usename từ token
    public String extractUserUsename(String token) {
        return extractAllClaims(token).getSubject();
    }

    // Lấy tất cả thông tin claims
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    // Kiểm tra token còn hợp lệ
    public boolean isTokenValid(String token, String username) {
        return username.equals(extractUserUsename(token)) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    //lấy ra thời gian của token
    public long getExpired(String token) {
        Claims claims = extractAllClaims(token);
        return claims.getExpiration().getTime();
    }

//    public Integer extractUserIdFromRequest(HttpServletRequest request) {
//        String tokenHeader = request.getHeader("Authorization");
//        if (tokenHeader == null || !tokenHeader.startsWith("Bearer ")) {
//            throw new RuntimeException("Token không hợp lệ hoặc thiếu.");
//        }
//
//        String token = tokenHeader.substring(7);
//        String email = extractUserEmail(token);
//        Optional<User> optional = .getByEmailOptional(email);
//
//        return optional
//                .map(NguoiDung::getNguoiDungId)
//                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với email: " + email));
//    }


}
