package com.food.rangolegal.repository;

import com.food.rangolegal.model.Restaurant;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long> {
    static boolean existsByName(@NotBlank String name) {
        return false;
    }
    List<Restaurant> findByNameContainingIgnoreCase(String name);
}
