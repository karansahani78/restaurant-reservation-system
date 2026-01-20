package com.karan.restaurant_reservation_system.service;

import com.karan.restaurant_reservation_system.dto.ResetPasswordRequest;
import com.karan.restaurant_reservation_system.entity.Admin;
import com.karan.restaurant_reservation_system.entity.PasswordResetToken;
import com.karan.restaurant_reservation_system.repository.AdminRepository;
import com.karan.restaurant_reservation_system.repository.PasswordResetTokenRepository;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepo;
    private final AdminRepository adminRepo;
    private final PasswordEncoder encoder;
    private final JavaMailSender mail;

    public PasswordResetService(
            PasswordResetTokenRepository tokenRepo,
            AdminRepository adminRepo,
            PasswordEncoder encoder,
            JavaMailSender mail) {

        this.tokenRepo = tokenRepo;
        this.adminRepo = adminRepo;
        this.encoder = encoder;
        this.mail = mail;
    }

    public void forgotPassword(String email) {

        String token = UUID.randomUUID().toString();

        tokenRepo.save(new PasswordResetToken(
                null,
                token,
                email,
                LocalDateTime.now().plusMinutes(15)
        ));

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(email);
        msg.setSubject("Reset Password");
        msg.setText(
                "Reset your password using this link:\n\n" +
                        "https://frontend/reset?token=" + token +
                        "\n\nThis link expires in 15 minutes."
        );

        mail.send(msg);
    }

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
