package com.food.rangolegal.dto;

public record PasswordUpdateDTO(String currentPassword, String newPassword, String confirmNewPassword) {
}
