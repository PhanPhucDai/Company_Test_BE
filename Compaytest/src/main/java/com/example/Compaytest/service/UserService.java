package com.example.Compaytest.service;

import com.example.Compaytest.dto.UserDTO;
import com.example.Compaytest.entity.User;
import com.example.Compaytest.repository.UserRepo;
import com.example.Compaytest.exception.NotificationCode;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
public class UserService implements UserDetailsService {

    private final UserRepo userRepo;

    public UserService(UserRepo userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        User user = userRepo.findByUsername(userName)
                .orElseThrow(() -> new UsernameNotFoundException("Tài khoản không được tìm thấy"));

        //  giả lập ROLE_USER
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }

    @Transactional
    public ServiceResponse<User> save(UserDTO userDTO) {
        try {
            User user = User.builder()
                    .fullName(userDTO.getFullName())
                    .email(userDTO.getEmail())
                    .username(userDTO.getUsername())
                    .password(new BCryptPasswordEncoder().encode(userDTO.getPassword()))
                    .build();
            User savedUser = userRepo.save(user);
            return ServiceResponse.success(NotificationCode.USER_CREATE_SUCCESS, savedUser);
        } catch (Exception e) {
            return ServiceResponse.error(NotificationCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Transactional
    public ServiceResponse<User> findByUsername(String username) {
        try {
            Optional<User> userOpt = userRepo.findByUsername(username);
            if (userOpt.isPresent()) {
                return ServiceResponse.success(NotificationCode.USER_FOUND, userOpt.get());
            } else {
                return ServiceResponse.error(NotificationCode.USER_NOT_FOUND);
            }

        } catch (Exception e) {
            return ServiceResponse.error(NotificationCode.INTERNAL_SERVER_ERROR);
        }
    }

    public boolean existsByUsername(String username) {
        return userRepo.existsByUsername(username);
    }

    // Kiểm tra tồn tại email
    public boolean existsByEmail(String email) {
        return userRepo.existsByEmail(email);
    }
}
