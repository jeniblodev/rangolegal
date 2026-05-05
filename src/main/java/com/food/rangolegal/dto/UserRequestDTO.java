package com.food.rangolegal.dto;

import com.food.rangolegal.model.Address;

public record UserRequestDTO(String name, String email, String login, String password, Address address, String userType) {
}
