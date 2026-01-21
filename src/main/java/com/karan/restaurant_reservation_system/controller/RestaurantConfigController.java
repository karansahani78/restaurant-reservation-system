package com.karan.restaurant_reservation_system.controller;

import com.karan.restaurant_reservation_system.entity.RestaurantConfig;
import com.karan.restaurant_reservation_system.repository.RestaurantConfigRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin/config")
public class RestaurantConfigController {

    private final RestaurantConfigRepository repository;

    public RestaurantConfigController(RestaurantConfigRepository repository) {
        this.repository = repository;
    }

    // ✅ GET CURRENT CONFIG
    @GetMapping
    public ResponseEntity<RestaurantConfig> getConfig() {
        RestaurantConfig config = repository.findFirstByOrderByIdAsc()
                .orElseThrow(() -> new RuntimeException("Restaurant config not found"));
        return ResponseEntity.ok(config);
    }

    // ✅ UPDATE CONFIG (OWNER + ADMIN)
    @PutMapping
    public ResponseEntity<RestaurantConfig> updateConfig(
            @RequestBody RestaurantConfig updated) {

        RestaurantConfig config = repository.findFirstByOrderByIdAsc()
                .orElseThrow(() -> new RuntimeException("Restaurant config not found"));

        config.setMaxCapacity(updated.getMaxCapacity());
        config.setSlotDurationMinutes(updated.getSlotDurationMinutes());

        return ResponseEntity.ok(repository.save(config));
    }
}
