package com.karan.restaurant_reservation_system.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "restaurant_config")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RestaurantConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int maxCapacity = 80;
    private int slotDurationMinutes = 30;

}
