package com.karan.restaurant_reservation_system.service;

import com.karan.restaurant_reservation_system.dto.*;
import com.karan.restaurant_reservation_system.entity.*;
import com.karan.restaurant_reservation_system.repository.*;
import com.karan.restaurant_reservation_system.security.JwtUtil;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AdminRepository adminRepo;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;

    public AuthService(AdminRepository adminRepo,
                       PasswordResetTokenRepository tokenRepo,
                       PasswordEncoder encoder,
                       JwtUtil jwtUtil) {
        this.adminRepo = adminRepo;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse login(AuthRequest req) {
        Admin admin = adminRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!encoder.matches(req.getPassword(), admin.getPassword()))
            throw new RuntimeException("Invalid credentials");

        return new AuthResponse(
                jwtUtil.generate(admin.getEmail(), admin.getRole().name()));
    }

    public void createAdmin(AuthRequest req) {
        adminRepo.save(Admin.builder()
                .email(req.getEmail())
                .password(encoder.encode(req.getPassword()))
                .role(Role.ADMIN)
                .build());
    }
}
