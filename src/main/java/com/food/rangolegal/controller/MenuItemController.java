package com.food.rangolegal.controller;

import com.food.rangolegal.dto.MenuItemRequestDTO;
import com.food.rangolegal.model.MenuItem;
import com.food.rangolegal.service.MenuItemService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "MenuItem", description = "Controle de itens do cardápio")
@RestController
@RequestMapping("/v1/menu_item")
public class MenuItemController {
    private final MenuItemService service;

    public MenuItemController(MenuItemService service) {
        this.service = service;
    }

    @Operation(summary = "Registrar novo item no cardápio")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Item cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro item já cadastrado")
    })
    @PostMapping
    public ResponseEntity<MenuItem> create(@RequestBody @Valid MenuItemRequestDTO menuItemRequestDTO) {
        return ResponseEntity.status(201).body(service.save(menuItemRequestDTO));
    }

    @Operation(summary = "Buscar refeição pelo nome no cardápio")
    @ApiResponse(responseCode = "200", description = "Refeição não encontrada")
    @GetMapping
    public ResponseEntity<List<MenuItem>> getByName(@RequestParam String name) {
        return ResponseEntity.ok(service.findByName(name));
    }

    @Operation(summary = "Atualizar refeição no Cardápio")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Refeição cadastrada com sucesso"),
            @ApiResponse(responseCode = "404", description = "Refeição não atualizada")
    })
    @PatchMapping("/{id}/data")
    public ResponseEntity<MenuItem> updateData(@PathVariable Long id, @RequestBody @Valid MenuItemRequestDTO menuItemRequestDTO) {
        return ResponseEntity.ok(service.updateData(id, menuItemRequestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok("Refeição deletada com sucesso");
    }
}
