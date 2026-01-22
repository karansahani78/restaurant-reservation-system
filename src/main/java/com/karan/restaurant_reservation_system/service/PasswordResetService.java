package com.karan.restaurant_reservation_system.service;

import com.karan.restaurant_reservation_system.dto.ResetPasswordRequest;
import com.karan.restaurant_reservation_system.entity.Admin;
import com.karan.restaurant_reservation_system.entity.PasswordResetToken;
import com.karan.restaurant_reservation_system.entity.Role;
import com.karan.restaurant_reservation_system.repository.AdminRepository;
import com.karan.restaurant_reservation_system.repository.PasswordResetTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class PasswordResetService {

    @Value("${brevo.api.key}")
    private String brevoApiKey;

    private static final String FROM_EMAIL = "codewithkaran723@gmail.com";
    private static final String FROM_NAME = "Malt Restaurant";

    private final PasswordResetTokenRepository tokenRepo;
    private final AdminRepository adminRepo;
    private final PasswordEncoder encoder;

    private final RestTemplate restTemplate = new RestTemplate();

    public PasswordResetService(
            PasswordResetTokenRepository tokenRepo,
            AdminRepository adminRepo,
            PasswordEncoder encoder) {

        this.tokenRepo = tokenRepo;
        this.adminRepo = adminRepo;
        this.encoder = encoder;
    }

    // ===============================
    // FORGOT PASSWORD
    // ===============================
    public void forgotPassword(String email) {

        Admin admin = adminRepo.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        if (admin.getRole() == Role.OWNER) {
            throw new RuntimeException("Owner password reset is not allowed");
        }

        String token = UUID.randomUUID().toString();

        tokenRepo.save(new PasswordResetToken(
                null,
                token,
                email,
                LocalDateTime.now().plusMinutes(15)
        ));

        sendResetEmail(email, token);
    }

    // ===============================
    // SEND EMAIL VIA BREVO HTTP API
    // ===============================
    private void sendResetEmail(String toEmail, String token) {

        String url = "https://api.brevo.com/v3/smtp/email";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 🔥 THIS HEADER IS THE KEY FIX
        headers.set("api-key", brevoApiKey);

        Map<String, Object> payload = new HashMap<>();
        payload.put("sender", Map.of(
                "email", FROM_EMAIL,
                "name", FROM_NAME
        ));
        payload.put("to", List.of(
                Map.of("email", toEmail)
        ));
        payload.put("subject", "Reset Your Password");
        payload.put(
                "htmlContent",
                "<p>Click the link below to reset your password:</p>" +
                        "<p><a href='https://frontend/reset?token=" + token + "'>Reset Password</a></p>" +
                        "<p>This link will expire in 15 minutes.</p>"
        );

        HttpEntity<Map<String, Object>> request =
                new HttpEntity<>(payload, headers);

        ResponseEntity<String> response =
                restTemplate.exchange(url, HttpMethod.POST, request, String.class);

        if (!response.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("Failed to send reset email via Brevo");
        }
    }

    // ===============================
    // RESET PASSWORD
    // ===============================
    public void resetPassword(ResetPasswordRequest req) {

        PasswordResetToken token = tokenRepo.findByToken(req.getToken())
                .orElseThrow(() -> new RuntimeException("Invalid reset token"));

        if (token.getExpiry().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Reset token expired");
        }

        Admin admin = adminRepo.findByEmail(token.getEmail())
                .orElseThrow(() -> new RuntimeException("Admin not found"));

        admin.setPassword(encoder.encode(req.getNewPassword()));
        adminRepo.save(admin);

        tokenRepo.delete(token);
    }
}
