package com.food.rangolegal.domain.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("RESTAURANT_OWNER")
public class RestaurantOwner extends User {
}
