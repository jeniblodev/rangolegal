package com.food.rangolegal.controller;

import com.food.rangolegal.dto.PasswordUpdateDTO;
import com.food.rangolegal.dto.UserRequestDTO;
import com.food.rangolegal.dto.UserUpdateDTO;
import com.food.rangolegal.model.User;
import com.food.rangolegal.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Tag(name = "users")
@RequestMapping("/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Registrar novo usuário")
    @PostMapping
    public ResponseEntity<User> create(@RequestBody UserRequestDTO userRequestDTO) {
        return ResponseEntity.status(201).body(userService.save(userRequestDTO));
    }

    @Operation(summary = "Buscar usuários pelo nome")
    @GetMapping
    public ResponseEntity<List<User>> getByName(@RequestParam String name) {
        return ResponseEntity.ok(userService.findByName(name));
    }

    @Operation(summary = "Atualizar dados cadastrais")
    @PatchMapping("/{id}/data")
    public ResponseEntity<User> updateData(@PathVariable Long id, @RequestBody UserUpdateDTO userUpdateDTO) {
        return ResponseEntity.ok(userService.updateData(id, userUpdateDTO));
    }

    @Operation(summary = "Trocar senha")
    @PatchMapping("/{id}/password")
    public ResponseEntity<Void> updatePassword(@PathVariable Long id, @RequestBody PasswordUpdateDTO passwordUpdateDTO) {
        userService.updatePassword(id, passwordUpdateDTO);
        return ResponseEntity.noContent().build();
    }
}
