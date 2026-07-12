package com.food.rangolegal.service;

import com.food.rangolegal.dto.RestaurantRequestDTO;
import com.food.rangolegal.dto.RestaurantUpdateDTO;
import com.food.rangolegal.model.Restaurant;
import com.food.rangolegal.model.RestaurantOwner;
import com.food.rangolegal.repository.RestaurantRepository;
import com.food.rangolegal.repository.RestaurantOwnerRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class RestaurantService {

    private final RestaurantRepository repository;
    private final RestaurantOwnerRepository restaurantOwnerRepository;

    public RestaurantService(RestaurantRepository repository, RestaurantOwnerRepository restaurantOwnerRepository) {
        this.repository = repository;
        this.restaurantOwnerRepository = restaurantOwnerRepository;
    }

    public List<Restaurant> listAll() {
        return repository.findAll();
    }

    @Transactional
    public Restaurant save(RestaurantRequestDTO dto) {

        boolean nomeJaExiste = RestaurantRepository.existsByName(dto.name());
        if (nomeJaExiste) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erro: Já existe um restaurante cadastrado com o nome '" + dto.name() + "'.");
        }

        if (dto.owner() == null || dto.owner().getId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erro: O proprietário do restaurante é obrigatório.");
        }

        boolean proprietarioExiste = restaurantOwnerRepository.existsById(dto.owner().getId());
        if (!proprietarioExiste) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Erro: Proprietário informado não encontrado.");
        }

        Restaurant restaurant = new Restaurant();
        restaurant.setName(dto.name());
        restaurant.setCuisineType(dto.cuisineType());
        restaurant.setOperatingHours(dto.operatingHours());
        restaurant.setAddress(dto.address());
        restaurant.setOwnerId(dto.owner().getId());

        return repository.save(restaurant);
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }

    public List<Restaurant> findByName(String name) {
        return repository.findByNameContainingIgnoreCase(name);
    }

    public Restaurant updateData(Long id, @Valid RestaurantUpdateDTO restaurantUpdateDTO) {
        Restaurant restaurantExistente = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Restaurante não encontrado"));

        restaurantExistente.setName(restaurantUpdateDTO.name());
        restaurantExistente.setCuisineType(restaurantUpdateDTO.cuisineType());
        restaurantExistente.setOperatingHours(restaurantUpdateDTO.operatingHours());
        restaurantExistente.setAddress(restaurantUpdateDTO.address());

        if (restaurantUpdateDTO.ownerId() != null) {
            boolean proprietarioExiste = restaurantOwnerRepository.existsById(restaurantUpdateDTO.ownerId());
            if (!proprietarioExiste) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Erro: Proprietário informado não encontrado.");
            }
            restaurantExistente.setOwnerId(restaurantUpdateDTO.ownerId());
        }

        return repository.save(restaurantExistente);
    }
}