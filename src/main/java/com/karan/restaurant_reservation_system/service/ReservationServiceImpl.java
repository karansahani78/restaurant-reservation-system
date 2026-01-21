package com.karan.restaurant_reservation_system.service;

import com.karan.restaurant_reservation_system.dto.ReservationRequest;
import com.karan.restaurant_reservation_system.dto.ReservationResponse;
import com.karan.restaurant_reservation_system.entity.Reservation;
import com.karan.restaurant_reservation_system.entity.ReservationStatus;
import com.karan.restaurant_reservation_system.entity.RestaurantConfig;
import com.karan.restaurant_reservation_system.exception.ResourceNotFoundException;
import com.karan.restaurant_reservation_system.repository.ReservationRepository;
import com.karan.restaurant_reservation_system.repository.RestaurantConfigRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class ReservationServiceImpl implements ReservationService {

    private final ReservationRepository reservationRepository;
    private final RestaurantConfigRepository configRepository;

    public ReservationServiceImpl(
            ReservationRepository reservationRepository,
            RestaurantConfigRepository configRepository
    ) {
        this.reservationRepository = reservationRepository;
        this.configRepository = configRepository;
    }

    @Override
    public ReservationResponse createReservation(ReservationRequest dto) {

        RestaurantConfig config = configRepository.findFirstByOrderByIdAsc()
                .orElseThrow(() -> new ResourceNotFoundException("Restaurant config not found"));

        List<Reservation> existing =
                reservationRepository.findByReservationDateAndReservationTime(
                        dto.getReservationDate(),
                        dto.getReservationTime()
                );

        int totalGuests = existing.stream()
                .filter(r -> r.getStatus() != ReservationStatus.CANCELLED)
                .mapToInt(Reservation::getGuestCount)
                .sum();

        Reservation reservation = new Reservation();
        reservation.setCustomerName(dto.getCustomerName());
        reservation.setPhone(dto.getPhone());
        reservation.setReservationDate(dto.getReservationDate());
        reservation.setReservationTime(dto.getReservationTime());
        reservation.setGuestCount(dto.getGuestCount());
        reservation.setNote(dto.getNote());

        if (totalGuests + dto.getGuestCount() > config.getMaxCapacity()) {
            reservation.setStatus(ReservationStatus.WAITING);
        } else {
            reservation.setStatus(ReservationStatus.RESERVED);
        }

        Reservation saved = reservationRepository.save(reservation);

        return ReservationResponse.builder()
                .id(saved.getId())
                .reservationDate(saved.getReservationDate())
                .reservationTime(saved.getReservationTime())
                .guestCount(saved.getGuestCount())
                .status(saved.getStatus())
                .createdAt(saved.getCreatedAt())

                // ✅ INCLUDED FOR SUCCESS PAGE
                .customerName(saved.getCustomerName())
                .phone(saved.getPhone())
                .note(saved.getNote())

                .message(
                        saved.getStatus() == ReservationStatus.WAITING
                                ? "Added to waiting list"
                                : "Reservation confirmed"
                )
                .build();
    }

    @Override
    public List<ReservationResponse> getTodayReservations() {
        return reservationRepository
                .findByReservationDateGreaterThanEqual(LocalDate.now())
                .stream()
                .map(r -> ReservationResponse.builder()
                        .id(r.getId())
                        .reservationDate(r.getReservationDate())
                        .reservationTime(r.getReservationTime())
                        .guestCount(r.getGuestCount())
                        .status(r.getStatus())
                        .createdAt(r.getCreatedAt())

                        // ✅ CRITICAL FIX FOR ADMIN DASHBOARD
                        .customerName(r.getCustomerName())
                        .phone(r.getPhone())
                        .note(r.getNote())

                        .build()
                )
                .toList();
    }

    @Override
    public void updateStatus(Long id, ReservationStatus status) {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Reservation not found"));

        reservation.setStatus(status);
        reservationRepository.save(reservation);
    }
}
