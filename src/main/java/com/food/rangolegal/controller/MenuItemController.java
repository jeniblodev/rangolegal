package com.food.rangolegal.controller;

import com.food.rangolegal.dto.MenuItemRequestDTO;
import com.food.rangolegal.model.MenuItem;
import com.food.rangolegal.repository.MenuItemRepository;
import com.food.rangolegal.service.MenuItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Service
public class MenuItemController {
    @Autowired
    private MenuItemRepository repository;
    private MenuItemService service;

    @Operation(summary = "Registrar novo item no cardápio")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Item cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro item já cadastrado")
    })
    @PostMapping
    public ResponseEntity<MenuItem> create(@RequestBody @Valid MenuItemRequestDTO menuItemRequestDTO) {
        return ResponseEntity.status(201).body(service.save(menuItemRequestDTO));
    }

    public List<MenuItem> listAll() {
        return repository.findAll();
    }

    public MenuItem save(MenuItem menuItem) {
        return repository.save(menuItem);
    }

    public MenuItem getById(Long id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Item não encontrado"));
    }

    public void delete(Long id) {
        repository.deleteById(id);
    }
}
