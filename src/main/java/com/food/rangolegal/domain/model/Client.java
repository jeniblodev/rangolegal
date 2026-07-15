package com.food.rangolegal.domain.model;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;

@Entity
@DiscriminatorValue("CLIENT")
public class Client extends User {
}
