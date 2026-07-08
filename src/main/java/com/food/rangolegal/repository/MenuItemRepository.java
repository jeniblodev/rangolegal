package com.food.rangolegal.repository;

import com.food.rangolegal.model.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MenuItemRepository extends JpaRepository<MenuItem, Long> {
    // Busca por parte do nome
    List<MenuItem> findByNameContainingIgnoreCase(String name);
    // Traz todos os itens do cardápio de um restaurante específico
    List<MenuItem> findByRestaurantId(Long restaurantId);
    // Busca um prato pelo nome APENAS dentro daquele restaurante
    List<MenuItem> findByRestaurantIdAndNameContainingIgnoreCase(Long restaurantId, String name);
}
