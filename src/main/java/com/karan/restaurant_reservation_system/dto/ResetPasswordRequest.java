package com.karan.restaurant_reservation_system.dto;

import lombok.*;

@Getter @Setter
public class ResetPasswordRequest {
    private String token;
    private String newPassword;
}
