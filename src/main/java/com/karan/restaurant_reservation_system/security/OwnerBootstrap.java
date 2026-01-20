package com.karan.restaurant_reservation_system.security;

import com.karan.restaurant_reservation_system.entity.*;
import com.karan.restaurant_reservation_system.repository.AdminRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class OwnerBootstrap {

    private final AdminRepository repo;
    private final PasswordEncoder encoder;

    public OwnerBootstrap(AdminRepository repo, PasswordEncoder encoder) {
        this.repo = repo;
        this.encoder = encoder;
    }

    @PostConstruct
    public void init() {
        if (repo.count() == 0) {
            repo.save(Admin.builder()
                    .email("codewithkaran723@gmail.com")
                    .password(encoder.encode("code321@"))
                    .role(Role.OWNER)
                    .build());
        }
    }
}
