package com.food.rangolegal.controller;

import com.food.rangolegal.application.dto.RestaurantRequestDTO;
import com.food.rangolegal.domain.model.Restaurant;
import com.food.rangolegal.application.service.RestaurantService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Restaurantes", description = "Gerenciamento de Restaurantes")
@RestController
@RequestMapping("/v1/restaurant")
public class RestaurantController {

    private final RestaurantService service;

    public RestaurantController(RestaurantService service) {
        this.service = service;
    }

    @Operation(summary = "Registrar novo restaurante")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Restaurante criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação ou e-mail duplicado")
    })

    @PostMapping
    public ResponseEntity<Restaurant> create(@RequestBody @Valid RestaurantRequestDTO dto) {
        return ResponseEntity.status(201).body(service.save(dto));
    }

    @Operation(summary = "Buscar restaurantes pelo nome")
    @ApiResponse(responseCode = "200", description = "Lista de restaurantes encontrados")
    @GetMapping
    public ResponseEntity<List<Restaurant>> getAll(@RequestParam(required = false) String name) {
        if (name != null && !name.isBlank()) {
            return ResponseEntity.ok(service.findByName(name));
        }

        return ResponseEntity.ok(service.listAll());
    }

    @Operation(summary = "Buscar restaurante por ID")
    @ApiResponse(responseCode = "200", description = "Restaurante encontrado")
    @GetMapping("/{id}")
    public ResponseEntity<Restaurant> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @Operation(summary = "Atualizar dados cadastrais")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dados atualizados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @PatchMapping("/{id}/data")
    public ResponseEntity<Restaurant> updateData(@PathVariable Long id, @RequestBody @Valid RestaurantRequestDTO restaurantUpadateDTO) {
        return ResponseEntity.ok(service.updateData(id, restaurantUpadateDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok("Restaurante deletado com sucesso");
    }
}
