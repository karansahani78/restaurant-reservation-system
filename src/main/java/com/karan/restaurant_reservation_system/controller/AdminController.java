package com.karan.restaurant_reservation_system.controller;

import com.karan.restaurant_reservation_system.dto.ReservationResponse;
import com.karan.restaurant_reservation_system.entity.ReservationStatus;
import com.karan.restaurant_reservation_system.service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("api/v1/admin")
public class AdminController {

    private final ReservationService reservationService;

    public AdminController(ReservationService reservationService) {
        this.reservationService = reservationService;
    }


    @GetMapping("/reservations")
    public ResponseEntity<List<ReservationResponse>> getReservations() {
        return ResponseEntity.ok(
                reservationService.getTodayReservations()
        );
    }


    @PostMapping("/status/{id}")
    public ResponseEntity<Void> updateStatus(
            @PathVariable Long id,
            @RequestParam ReservationStatus status) {

        reservationService.updateStatus(id, status);
        return ResponseEntity.noContent().build();
    }
}
