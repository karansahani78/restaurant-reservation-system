package com.karan.restaurant_reservation_system.repository;

import com.karan.restaurant_reservation_system.entity.RestaurantConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RestaurantConfigRepository extends JpaRepository<RestaurantConfig, Long> {
}
