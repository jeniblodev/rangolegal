package com.food.rangolegal.application.dto;

public record PasswordUpdateDTO(String currentPassword, String newPassword, String confirmNewPassword) {
}
