package com.food.rangolegal.dto;

import jakarta.validation.constraints.NotBlank;

public record UserTypeRequestDTO(
        @NotBlank String name) {
}
