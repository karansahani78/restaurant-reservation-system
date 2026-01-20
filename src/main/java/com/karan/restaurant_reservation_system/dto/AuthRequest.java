package com.karan.restaurant_reservation_system.dto;

import lombok.*;

@Getter @Setter
public class AuthRequest {
    private String email;
    private String password;
}
