package com.food.rangolegal.service;

import com.food.rangolegal.dto.MenuItemRequestDTO;
import com.food.rangolegal.model.MenuItem;
import com.food.rangolegal.model.Restaurant;
import com.food.rangolegal.repository.MenuItemRepository;
import com.food.rangolegal.repository.RestaurantRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MenuItemService {
    @Autowired
    private MenuItemRepository menuItemRepository;
    @Autowired
    private RestaurantRepository restaurantRepository;

    // Listar todos os itens de todos os restaurantes
    public List<MenuItem> listarTodos() {
        return menuItemRepository.findAll();
    }

    // Buscar um item específico pelo ID
    public MenuItem getById(Long id) {
        return menuItemRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Item do cardápio não encontrado com o ID: " + id));
    }

    // Salvar/Criar um novo item no cardápio
    public MenuItem save(@Valid MenuItemRequestDTO item) {
        if (item.restaurantId() == null) {
            throw new RuntimeException("O item do cardápio precisa estar vinculado a um Restaurante válido.");
        }

        Restaurant restaurant = restaurantRepository.findById(item.restaurantId())
                .orElseThrow(() -> new RuntimeException("Não é possível adicionar o item: Restaurante não encontrado"));

        MenuItem menuItem = new MenuItem();
        menuItem.setName(item.name());
        menuItem.setDescription(item.description());
        menuItem.setPrice(item.price());
        menuItem.setDineInOnly(item.dineInOnly());
        menuItem.setPhotoPath(item.photoPath());
        menuItem.setRestaurant(restaurant);

        return menuItemRepository.save(menuItem);
    }

    // Atualizar um item existente
    public MenuItem atualizar(Long id, MenuItem itemAtualizado) {
        MenuItem itemExistente = getById(id);

        itemExistente.setName(itemAtualizado.getName());
        itemExistente.setDescription(itemAtualizado.getDescription());
        itemExistente.setPrice(itemAtualizado.getPrice());
        itemExistente.setDineInOnly(itemAtualizado.isDineInOnly());
        itemExistente.setPhotoPath(itemAtualizado.getPhotoPath());

        return menuItemRepository.save(itemExistente);
    }

    // Deletar um item
    public void delete(Long id) {
        MenuItem item = getById(id);
        menuItemRepository.delete(item);
    }

}
