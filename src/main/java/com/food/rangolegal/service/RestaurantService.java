package com.food.rangolegal.service;

import com.food.rangolegal.dto.UserUpdateDTO;
import com.food.rangolegal.model.Restaurant;
import com.food.rangolegal.repository.RestaurantRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RestaurantService {

    private final RestaurantRepository repository;

    public RestaurantService(RestaurantRepository repository) {
        this.repository = repository;
    }

    public List<Restaurant> listAll() {
        return repository.findAll();
    }

    public Restaurant save(Restaurant restaurant) {
        return repository.save(restaurant);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public List<Restaurant> findByName(String name) {
        return repository.findByNameContainingIgnoreCase(name);
    }

    public Restaurant updateData(Long id, @Valid UserUpdateDTO userUpdateDTO) {
        return null;
    }
}