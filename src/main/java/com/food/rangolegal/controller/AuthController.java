package com.food.rangolegal.controller;

import com.food.rangolegal.dto.LoginRequestDTO;
import com.food.rangolegal.service.AuthService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "auth", description = "Endpoints de autenticação")
@RestController
@RequestMapping("/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        if (authService.validateLogin(loginRequestDTO)) {
            return ResponseEntity.ok("Login realizado com sucesso");
        }
    return ResponseEntity.status(404).body("Dados incorretos");
    }
}
