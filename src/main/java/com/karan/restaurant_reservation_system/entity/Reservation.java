package com.karan.restaurant_reservation_system.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "reservations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerName;
    private String phone;

    // ✅ REAL reservation info
    private LocalDate reservationDate;
    private LocalTime reservationTime;

    private int guestCount;

    @Enumerated(EnumType.STRING)
    private ReservationStatus status;

    private String note;

    // ✅ AUDIT FIELD (IMPORTANT)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
