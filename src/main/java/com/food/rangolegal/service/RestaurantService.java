package com.food.rangolegal.service;

import com.food.rangolegal.model.Restaurant;
import com.food.rangolegal.repository.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RestaurantService {

    @Autowired
    private RestaurantRepository repository;

    public List<Restaurant> listAll() {
        return repository.findAll();
    }

    public Restaurant save(Restaurant restaurant) {
        return repository.save(restaurant);
    }

    public Restaurant getById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Restaurante não encontrado"));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}