package com.example.Compaytest.security;

import com.example.Compaytest.repository.BlackListRepo;
import com.example.Compaytest.service.JwtService;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtFillter extends OncePerRequestFilter {

    private JwtService jwtService;
    private BlackListRepo blacklistRepository;

    public JwtFillter(JwtService jwtService, BlackListRepo blacklistRepository) {
        this.jwtService = jwtService;
        this.blacklistRepository = blacklistRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();

        // Lấy token từ header
        String authorization = request.getHeader("Authorization");
        String token = null;
        String username = null;

        if (path.startsWith("/auth/login") || path.startsWith("/auth/register")) {
            filterChain.doFilter(request, response);
            return;
        }

        if (authorization != null && authorization.startsWith("Bearer ")) {
            token = authorization.replace("Bearer ", "");
            username = jwtService.extractUserUsename(token);
        }

        // Nếu token nằm trong blacklist
        if (token != null && blacklistRepository.existsByTokenBlacklist(token)) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Token is blacklisted");
            return;
        }

        // Nếu chưa được xác thực trong context thì xử lý
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            Claims claims = jwtService.extractAllClaims(token);
            List<String> roles = claims.get("roles", List.class);

            if (roles == null || roles.isEmpty()) {
                response.setStatus(HttpStatus.FORBIDDEN.value());
                response.getWriter().write("Token không chứa quyền truy cập");
                return;
            }

            List<GrantedAuthority> authorities = roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
            UserDetails userDetails = new User(username, "", authorities);

            if (jwtService.isTokenValid(token, userDetails.getUsername())) {
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
            }
        }
        filterChain.doFilter(request, response);
    }
}


