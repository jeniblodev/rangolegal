package com.food.rangolegal.application.dto;

import com.food.rangolegal.domain.model.Address;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RestaurantRequestDTO(
        @NotBlank String name,
        @NotNull @Valid Address address,
        @NotBlank String cuisineType,
        @NotBlank String operatingHours,
        @NotNull Long ownerId
        )
{}
