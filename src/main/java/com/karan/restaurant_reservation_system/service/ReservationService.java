package com.karan.restaurant_reservation_system.service;

import com.karan.restaurant_reservation_system.dto.ReservationRequest;
import com.karan.restaurant_reservation_system.dto.ReservationResponse;
import com.karan.restaurant_reservation_system.entity.ReservationStatus;

import java.util.List;

public interface ReservationService {
    ReservationResponse createReservation(ReservationRequest dto);

    List<ReservationResponse> getTodayReservations();

    void updateStatus(Long id, ReservationStatus status);
}
