package com.food.rangolegal.controller;

import com.food.rangolegal.model.Restaurant;
import com.food.rangolegal.service.RestaurantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/restaurant")

public class RestaurantControlller {
    @Autowired
    private RestaurantService service;

    @GetMapping
    public List<Restaurant> list() {
        return service.listAll();
    }

    @PostMapping
    public Restaurant create(@RequestBody Restaurant restaurante) {
        return service.save(restaurante);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Restaurant> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}