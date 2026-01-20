package com.karan.restaurant_reservation_system.controller;

import com.karan.restaurant_reservation_system.dto.*;
import com.karan.restaurant_reservation_system.service.AuthService;
import com.karan.restaurant_reservation_system.service.PasswordResetService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService service;
    private final PasswordResetService passwordResetService;

    public AuthController(AuthService service, PasswordResetService passwordResetService) {
        this.passwordResetService = passwordResetService;
        this.service = service;
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest req) {
        return service.login(req);
    }
    @PostMapping("/forgot-password")
    public void forgot(@RequestParam String email) {
        passwordResetService.forgotPassword(email);
    }

    @PostMapping("/reset-password")
    public void reset(@RequestBody ResetPasswordRequest req) {
        passwordResetService.resetPassword(req);
    }
}
