package com.food.rangolegal.infrastructure.repository;

import com.food.rangolegal.domain.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    List<MenuItem> findByNameContainingIgnoreCase(String name);
    boolean existsByNameAndRestaurantId(String name, Long restaurantId);
    boolean existsByNameAndRestaurantIdAndIdNot(String name, Long restaurantId, Long id);
}
