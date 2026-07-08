package com.food.rangolegal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RestaurantRequestDTO(
        @NotBlank String name,
        @NotBlank String address,
        @NotBlank String cuisineType,
        @NotBlank String operatingHours,
        @NotNull String owner
        )
{}