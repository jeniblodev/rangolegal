package com.food.rangolegal.application.dto;

import com.food.rangolegal.domain.model.Address;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserRequestDTO(
        @NotBlank String name,
        @NotBlank @Email String email,
        @NotBlank String login,
        @NotBlank String password,
        @NotNull @Valid Address address,
        String userType) {
}
