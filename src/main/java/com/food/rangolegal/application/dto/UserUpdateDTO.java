package com.food.rangolegal.application.dto;

import com.food.rangolegal.domain.model.Address;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserUpdateDTO(
        @NotBlank String name,
        @NotBlank String login,
        @NotNull @Valid Address address) {
}
