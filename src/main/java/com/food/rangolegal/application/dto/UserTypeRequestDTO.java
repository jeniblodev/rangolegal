package com.food.rangolegal.application.dto;

import jakarta.validation.constraints.NotBlank;

public record UserTypeRequestDTO(
        @NotBlank String name) {
}
