package com.cts.IdentityService.service;


import com.cts.IdentityService.dto.*;
import com.cts.IdentityService.entity.*;
import com.cts.IdentityService.repository.*;
import com.cts.IdentityService.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepo;
    private final AuditLogRepository auditRepo;
    private final JwtUtil jwtUtil;

    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public String register(RegisterRequest request) {

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .password(encoder.encode(request.getPassword()))
                .role(Role.valueOf(request.getRole()))
                .build();

        userRepo.save(user);

        auditRepo.save(AuditLog.builder()
                .userID(user.getUserID())
                .action("USER_REGISTERED")
                .timestamp(LocalDateTime.now())
                .build());

        return "User Registered Successfully";
    }

    public AuthResponse login(LoginRequest request) {

        User user = userRepo.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!encoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        String token = jwtUtil.generateToken(user.getEmail(), user.getRole().name());

        auditRepo.save(AuditLog.builder()
                .userID(user.getUserID())
                .action("USER_LOGIN")
                .timestamp(LocalDateTime.now())
                .build());

        return new AuthResponse(token);
    }
}
