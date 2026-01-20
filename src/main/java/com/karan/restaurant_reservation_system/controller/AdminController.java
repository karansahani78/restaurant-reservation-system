package com.karan.restaurant_reservation_system.controller;

import com.karan.restaurant_reservation_system.dto.AuthRequest;
import com.karan.restaurant_reservation_system.dto.ReservationResponse;
import com.karan.restaurant_reservation_system.entity.ReservationStatus;
import com.karan.restaurant_reservation_system.service.AuthService;
import com.karan.restaurant_reservation_system.service.ReservationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminController {

    private final ReservationService reservationService;
    private final AuthService authService;

    public AdminController(ReservationService reservationService,
                           AuthService authService) {
        this.reservationService = reservationService;
        this.authService = authService;
    }

    // ✅ VIEW DASHBOARD RESERVATIONS
    @GetMapping("/reservations")
    public ResponseEntity<List<ReservationResponse>> getReservations() {
        return ResponseEntity.ok(
                reservationService.getTodayReservations()
        );
    }

    // ✅ UPDATE RESERVATION STATUS
    @PostMapping("/status/{id}")
    public ResponseEntity<Void> updateStatus(
            @PathVariable Long id,
            @RequestParam ReservationStatus status) {

        reservationService.updateStatus(id, status);
        return ResponseEntity.noContent().build();
    }

    // ✅ CREATE NEW ADMIN (OWNER ONLY)
    @PostMapping("/create-admin")
    public ResponseEntity<Void> createAdmin(
            @RequestBody AuthRequest request) {

        authService.createAdmin(request);
        return ResponseEntity.ok().build();
    }
}
