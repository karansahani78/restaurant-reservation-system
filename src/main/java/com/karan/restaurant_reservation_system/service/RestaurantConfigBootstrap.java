package com.karan.restaurant_reservation_system.service;

import com.karan.restaurant_reservation_system.entity.RestaurantConfig;
import com.karan.restaurant_reservation_system.repository.RestaurantConfigRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Component;

@Component
public class RestaurantConfigBootstrap {

    private final RestaurantConfigRepository repo;

    public RestaurantConfigBootstrap(RestaurantConfigRepository repo) {
        this.repo = repo;
    }

    @PostConstruct
    public void init() {
        if (repo.count() == 0) {
            repo.save(RestaurantConfig.builder()
                    .maxCapacity(80)
                    .slotDurationMinutes(30)
                    .build());
        }
    }
}
