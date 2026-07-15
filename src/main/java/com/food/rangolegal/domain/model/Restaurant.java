package com.food.rangolegal.domain.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "restaurants")
@Getter
@Setter
public class Restaurant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Embedded
    private Address address;

    @Column(name = "cuisine_type", nullable = false)
    private String cuisineType;

    @Column(name = "operating_hours", nullable = false)
    private String operatingHours;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
}
