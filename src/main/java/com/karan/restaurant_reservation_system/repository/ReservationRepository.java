package com.karan.restaurant_reservation_system.repository;

import com.karan.restaurant_reservation_system.entity.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    List<Reservation> findByReservationDate(LocalDate date);

    List<Reservation> findByReservationDateAndReservationTime(
            LocalDate date, LocalTime time);

    // ✅ NEW: today + future reservations
    List<Reservation> findByReservationDateGreaterThanEqual(LocalDate date);
}
