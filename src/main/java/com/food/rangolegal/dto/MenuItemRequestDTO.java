package com.food.rangolegal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record MenuItemRequestDTO(
        @NotBlank String name,
        @NotBlank String description,
        @NotNull @Positive BigDecimal price,
        @NotNull Boolean dineInOnly,
        @NotBlank String photoPath,
        @NotNull Long restaurantId ) {
}
