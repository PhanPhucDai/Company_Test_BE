package com.example.Compaytest.controller;

import com.example.Compaytest.dto.LoginRequest;
import com.example.Compaytest.dto.UserDTO;
import com.example.Compaytest.service.BlackListService;
import com.example.Compaytest.service.JwtService;
import com.example.Compaytest.service.ServiceResponse;
import com.example.Compaytest.service.UserService;
import com.example.Compaytest.exception.NotificationCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/auth")
public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final BlackListService blackListService;


    public UserController(UserService userService, AuthenticationManager authenticationManager, JwtService jwtService, BlackListService blackListService) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.blackListService = blackListService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> dangNhap(@RequestBody LoginRequest loginRequest) {
        // Check username/password trống
        if (loginRequest.getUsername() == null || loginRequest.getUsername().isBlank() ||
                loginRequest.getPassword() == null || loginRequest.getPassword().isBlank()) {
            return ResponseEntity
                    .badRequest()
                    .body(ServiceResponse.error(NotificationCode.AUTH_LOGIN_NULL));
        }

        try {
            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword());
            Authentication authentication = authenticationManager.authenticate(authToken);
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtService.generateToken(userDetails.getUsername());
            return ResponseEntity.ok(ServiceResponse.success(NotificationCode.AUTH_LOGIN_SUCCESS, token));
        } catch (BadCredentialsException ex) {
            logger.warn("Login failed: bad credentials - " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(ServiceResponse.error(NotificationCode.AUTH_LOGIN_FAIL ));
        } catch (Exception ex) {
            logger.warn("Login failed: unexpected error - " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ServiceResponse.error(NotificationCode.AUTH_LOGIN_FAIL ));
        }
    }



    @PostMapping("/logout")
    public ResponseEntity<ServiceResponse<?>> dangXuat(HttpServletRequest request) {
        try {
            String authHeader = request.getHeader("Authorization");

            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                long millisLeft = jwtService.getExpired(token) - System.currentTimeMillis();

                if (millisLeft > 0) {
                    blackListService.save(token, millisLeft);
                    logger.info("Token đã được thêm vào danh sách đen, còn hiệu lực {} ms", millisLeft);
                } else {
                    logger.warn("Token đã hết hạn, không thêm vào danh sách đen");
                }
            } else {
                logger.warn("Không có header Authorization hoặc định dạng không hợp lệ");
            }

            return ResponseEntity.ok(ServiceResponse.success(NotificationCode.AUTH_LOGOUT_SUCCESS));

        } catch (Exception ex) {
            logger.error("Lỗi khi đăng xuất: {}", ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ServiceResponse.error(NotificationCode.INTERNAL_SERVER_ERROR));
        }
    }



    @PostMapping("/register")
    public ResponseEntity<?> dangKi(@Valid @RequestBody UserDTO UserDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            List<String> errors = bindingResult.getFieldErrors().stream()
                    .map(err -> {
                        try {
                            NotificationCode code = NotificationCode.valueOf(err.getDefaultMessage());
                            return err.getField() + ": " + code.getMessage();
                        } catch (IllegalArgumentException ex) {
                            return err.getField() + ": " + err.getDefaultMessage();
                        }
                    }).toList();
            return ResponseEntity.badRequest().body(
                    ServiceResponse.error(NotificationCode.VALIDATION_FAILED)
            );
        }

        try {
            ServiceResponse<?> response = userService.save(UserDTO);
            if (!response.isSuccess()) {
                return ResponseEntity.badRequest().body(response);
            }

            return ResponseEntity.ok(ServiceResponse.success(NotificationCode.USER_CREATE_SUCCESS));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ServiceResponse.error(NotificationCode.INTERNAL_SERVER_ERROR));
        }
    }

    // Check username tồn tại
    @GetMapping("/exists/username/{username}")
    public ResponseEntity<Boolean> checkUsername(@PathVariable String username) {
        boolean exists = userService.existsByUsername(username);
        return ResponseEntity.ok(exists);
    }

    // Check email tồn tại
    @GetMapping("/exists/email/{email}")
    public ResponseEntity<Boolean> checkEmail(@PathVariable String email) {
        boolean exists = userService.existsByEmail(email);
        return ResponseEntity.ok(exists);
    }
}