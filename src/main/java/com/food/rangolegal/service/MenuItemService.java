package com.food.rangolegal.service;

import com.food.rangolegal.model.MenuItem;
import com.food.rangolegal.repository.MenuItemRepository;
import com.food.rangolegal.repository.RestaurantRepository;
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
    public MenuItem save(MenuItem item) {
        if (item.getRestaurant() != null && item.getRestaurant().getId() != null) {
            restaurantRepository.findById(item.getRestaurant().getId())
                    .orElseThrow(() -> new RuntimeException("Não é possível adicionar o item: Restaurante não encontrado"));
        } else {
            throw new RuntimeException("O item do cardápio precisa estar vinculado a um Restaurante válido.");
        }
        return menuItemRepository.save(item);
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
