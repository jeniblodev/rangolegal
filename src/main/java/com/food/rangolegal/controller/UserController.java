package com.food.rangolegal.controller;

import com.food.rangolegal.dto.PasswordUpdateDTO;
import com.food.rangolegal.dto.UserRequestDTO;
import com.food.rangolegal.dto.UserUpdateDTO;
import com.food.rangolegal.model.User;
import com.food.rangolegal.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Users", description = "Gerenciamento de Donos de Restaurantes e Clientes")
@RestController
@RequestMapping("/v1/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Registrar novo usuário")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação ou e-mail duplicado")
    })

    @PostMapping
    public ResponseEntity<User> create(@RequestBody @Valid UserRequestDTO userRequestDTO) {
        return ResponseEntity.status(201).body(userService.save(userRequestDTO));
    }

    @Operation(summary = "Buscar usuários pelo nome")
    @ApiResponse(responseCode = "200", description = "Lista de usuários encontrada")
    @GetMapping
    public ResponseEntity<List<User>> getByName(@RequestParam String name) {
        return ResponseEntity.ok(userService.findByName(name));
    }

    @Operation(summary = "Atualizar dados cadastrais")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dados atualizados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    @PatchMapping("/{id}/data")
    public ResponseEntity<User> updateData(@PathVariable Long id, @RequestBody @Valid UserUpdateDTO userUpdateDTO) {
        return ResponseEntity.ok(userService.updateData(id, userUpdateDTO));
    }

    @Operation(summary = "Atualizar senha")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Senha alterada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Senha atual incorreta")
    })
    @PatchMapping("/{id}/password")
    public ResponseEntity<String> updatePassword(@PathVariable Long id, @RequestBody PasswordUpdateDTO passwordUpdateDTO) {
        userService.updatePassword(id, passwordUpdateDTO);
        return ResponseEntity.ok("Senha atualizada com sucesso");
    }
    @DeleteMapping("/{id}")

    public ResponseEntity<String> delete(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok("Usuário deletado com sucesso");
    }
}
