package com.food.rangolegal.dto;

import com.food.rangolegal.model.Address;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record UserUpdateDTO(
        @NotBlank String name,
        @NotBlank String login,
        @NotNull @Valid Address address) {
}
