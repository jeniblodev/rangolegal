package com.food.rangolegal.controller;

import com.food.rangolegal.application.dto.UserTypeRequestDTO;
import com.food.rangolegal.domain.model.UserType;
import com.food.rangolegal.application.service.UserTypeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "User Types", description = "Gerenciamento de tipos de usuario")
@RestController
@RequestMapping("/v1/user-types")
public class UserTypeController {

    private final UserTypeService service;

    public UserTypeController(UserTypeService service) {
        this.service = service;
    }

    @Operation(summary = "Registrar novo tipo de usuario")
    @PostMapping
    public ResponseEntity<UserType> create(@RequestBody @Valid UserTypeRequestDTO dto) {
        return ResponseEntity.status(201).body(service.create(dto));
    }

    @Operation(summary = "Listar tipos de usuario")
    @GetMapping
    public ResponseEntity<List<UserType>> listAll() {
        return ResponseEntity.ok(service.listAll());
    }

    @Operation(summary = "Buscar tipo de usuario por ID")
    @GetMapping("/{id}")
    public ResponseEntity<UserType> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @Operation(summary = "Atualizar tipo de usuario")
    @PutMapping("/{id}")
    public ResponseEntity<UserType> update(@PathVariable Long id, @RequestBody @Valid UserTypeRequestDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }

    @Operation(summary = "Remover tipo de usuario")
    @DeleteMapping("/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.ok("Tipo de usuario deletado com sucesso");
    }
}
