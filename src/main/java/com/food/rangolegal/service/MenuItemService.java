package com.food.rangolegal.service;

import com.food.rangolegal.dto.MenuItemRequestDTO;
import com.food.rangolegal.model.MenuItem;
import com.food.rangolegal.model.Restaurant;
import com.food.rangolegal.repository.MenuItemRepository;
import com.food.rangolegal.repository.RestaurantRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class MenuItemService {
    @Autowired
    private MenuItemRepository menuItemRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;

    public List<MenuItem> listarTodos() {
        return menuItemRepository.findAll();
    }

    public MenuItem getById(Long id) {
        return menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item do cardápio não encontrado com o ID: " + id));
    }

    public List<MenuItem> findByName(String name) {
        return menuItemRepository.findByNameContainingIgnoreCase(name);
    }

    @Transactional
    public MenuItem save(MenuItemRequestDTO item) {
        if (item.restaurantId() == null) {
            throw new RuntimeException("O item do cardápio precisa estar vinculado a um Restaurante válido.");
        }

        Restaurant restaurant = restaurantRepository.findById(item.restaurantId())
                .orElseThrow(() -> new RuntimeException("Não é possível adicionar o item: Restaurante não encontrado"));

        boolean nomeJaExiste = menuItemRepository.existsByNameAndRestaurantId(
                item.name(),
                item.restaurantId()
        );

        if (nomeJaExiste) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erro: Este restaurante já possui um item com o nome '" + item.name() + "'.");
        }
        MenuItem menuItem = new MenuItem();
        menuItem.setName(item.name());
        menuItem.setDescription(item.description());
        menuItem.setPrice(item.price());
        menuItem.setDineInOnly(item.dineInOnly());
        menuItem.setPhotoPath(item.photoPath());
        menuItem.setRestaurant(restaurant);

        return menuItemRepository.save(menuItem);
    }

    @Transactional
    public MenuItem updateData(Long id, MenuItemRequestDTO menuItemRequestDTO) {
        MenuItem itemExistente = getById(id);

        Long restaurantId = (menuItemRequestDTO.restaurantId() != null)
                ? menuItemRequestDTO.restaurantId()
                : itemExistente.getRestaurant().getId();

        boolean nomeJaExiste = menuItemRepository.existsByNameAndRestaurantIdAndIdNot(
                menuItemRequestDTO.name(),
                restaurantId,
                id
        );

        if (nomeJaExiste) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Erro: Já existe outro item com este nome neste restaurante.");
        }

        itemExistente.setName(menuItemRequestDTO.name());
        itemExistente.setDescription(menuItemRequestDTO.description());
        itemExistente.setPrice(menuItemRequestDTO.price());
        itemExistente.setDineInOnly(menuItemRequestDTO.dineInOnly());
        itemExistente.setPhotoPath(menuItemRequestDTO.photoPath());

        if (menuItemRequestDTO.restaurantId() != null) {
            Restaurant restaurant = restaurantRepository.findById(menuItemRequestDTO.restaurantId())
                    .orElseThrow(() -> new RuntimeException("Restaurante não encontrado"));
            itemExistente.setRestaurant(restaurant);
        }

        return menuItemRepository.save(itemExistente);
    }

    public void delete(Long id) {
        if (!menuItemRepository.existsById(id)) {
            throw new RuntimeException("Item do menu nao encontrado com o ID: " + id);
        }

        menuItemRepository.deleteById(id);
    }
}
