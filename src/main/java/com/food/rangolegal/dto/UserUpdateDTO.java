package com.food.rangolegal.dto;

import com.food.rangolegal.model.Address;

public record UserUpdateDTO(String name, String login, Address address) {
}
