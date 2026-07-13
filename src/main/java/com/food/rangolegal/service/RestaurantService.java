package com.food.rangolegal.service;

import com.food.rangolegal.dto.RestaurantRequestDTO;
import com.food.rangolegal.model.Restaurant;
import com.food.rangolegal.model.RestaurantOwner;
import com.food.rangolegal.model.User;
import com.food.rangolegal.repository.RestaurantRepository;
import com.food.rangolegal.repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RestaurantService {

    private final RestaurantRepository repository;
    private final UserRepository userRepository;

    public RestaurantService(RestaurantRepository repository, UserRepository userRepository) {
        this.repository = repository;
        this.userRepository = userRepository;
    }

    public List<Restaurant> listAll() {
        return repository.findAll();
    }

    public Restaurant findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Restaurante nao encontrado com o ID: " + id));
    }

    public Restaurant save(RestaurantRequestDTO dto) {
        Restaurant restaurant = new Restaurant();
        updateRestaurantData(restaurant, dto);
        return repository.save(restaurant);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("Restaurante nao encontrado com o ID: " + id);
        }
        repository.deleteById(id);
    }

    public List<Restaurant> findByName(String name) {
        return repository.findByNameContainingIgnoreCase(name);
    }

    public Restaurant updateData(Long id, RestaurantRequestDTO dto) {
        Restaurant restaurant = findById(id);
        updateRestaurantData(restaurant, dto);
        return repository.save(restaurant);
    }

    private void updateRestaurantData(Restaurant restaurant, RestaurantRequestDTO dto) {
        RestaurantOwner owner = findRestaurantOwner(dto.ownerId());

        restaurant.setName(dto.name());
        restaurant.setAddress(dto.address());
        restaurant.setCuisineType(dto.cuisineType());
        restaurant.setOperatingHours(dto.operatingHours());
        restaurant.setOwner(owner);
    }

    private RestaurantOwner findRestaurantOwner(Long ownerId) {
        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> new RuntimeException("Dono do restaurante nao encontrado com o ID: " + ownerId));

        if (!(user instanceof RestaurantOwner owner)) {
            throw new RuntimeException("Usuario informado nao e dono de restaurante");
        }

        return owner;
    }
}
