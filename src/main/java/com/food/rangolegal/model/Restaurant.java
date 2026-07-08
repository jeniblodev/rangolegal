package com.food.rangolegal.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.util.List;

@Entity
@Table(name = "tb_restaurants")
@Getter
@Setter

public class Restaurant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(name = "cuisine_type", nullable = false)
    private String cuisineType;

    @Column(name = "operating_hours", nullable = false)
    private String operatingHours;

    // Relacionamento com o Dono (Atribuindo um RestaurantOwner ao restaurante)
    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private RestaurantOwner owner;

    // Relacionamento com os itens do cardápio (Um restaurante tem muitos itens)
    @OneToMany(mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MenuItem> menuItems;

}
