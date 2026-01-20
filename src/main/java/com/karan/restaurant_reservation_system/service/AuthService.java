package com.karan.restaurant_reservation_system.service;

import com.karan.restaurant_reservation_system.dto.*;
import com.karan.restaurant_reservation_system.entity.*;
import com.karan.restaurant_reservation_system.repository.*;
import com.karan.restaurant_reservation_system.security.JwtUtil;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class AuthService {

    private final AdminRepository adminRepo;
    private final PasswordResetTokenRepository tokenRepo;
    private final PasswordEncoder encoder;
    private final JwtUtil jwtUtil;
    private final JavaMailSender mail;

    public AuthService(AdminRepository adminRepo,
                       PasswordResetTokenRepository tokenRepo,
                       PasswordEncoder encoder,
                       JwtUtil jwtUtil,
                       JavaMailSender mail) {
        this.adminRepo = adminRepo;
        this.tokenRepo = tokenRepo;
        this.encoder = encoder;
        this.jwtUtil = jwtUtil;
        this.mail = mail;
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

    public void forgotPassword(String email) {
        String token = UUID.randomUUID().toString();

        tokenRepo.save(new PasswordResetToken(
                null, token, email, LocalDateTime.now().plusMinutes(15)));

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(email);
        msg.setSubject("Reset Password");
        msg.setText("Reset link: https://frontend/reset?token=" + token);
        mail.send(msg);
    }

    public void resetPassword(ResetPasswordRequest req) {
        PasswordResetToken token = tokenRepo.findByToken(req.getToken())
                .orElseThrow();

        if (token.getExpiry().isBefore(LocalDateTime.now()))
            throw new RuntimeException("Token expired");

        Admin admin = adminRepo.findByEmail(token.getEmail()).orElseThrow();
        admin.setPassword(encoder.encode(req.getNewPassword()));
        adminRepo.save(admin);
        tokenRepo.delete(token);
    }
}
