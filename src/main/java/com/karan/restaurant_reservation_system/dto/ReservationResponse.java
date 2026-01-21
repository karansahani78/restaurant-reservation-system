package com.karan.restaurant_reservation_system.dto;

import com.karan.restaurant_reservation_system.entity.ReservationStatus;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationResponse {

    private Long id;

    // ✅ REAL booking data (THIS FIXES YOUR ISSUE)
    private LocalDate reservationDate;
    private LocalTime reservationTime;
    private int guestCount;

    private ReservationStatus status;

    // audit info (optional, still useful)
    private LocalDateTime createdAt;
    // ✅ ADD THIS
    private String note;
    // message used only for success page
    private String message;
}
