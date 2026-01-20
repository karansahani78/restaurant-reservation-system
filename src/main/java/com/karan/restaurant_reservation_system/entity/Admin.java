package com.karan.restaurant_reservation_system.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "admins")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String password;

    @Enumerated(EnumType.STRING)
    private Role role;
}
