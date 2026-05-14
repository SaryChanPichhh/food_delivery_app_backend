package com.example.food_delivery_app.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
@Data
@Entity
@Table(name = "favorites")
public class FavoritesModel {
    @GeneratedValue(strategy =  GenerationType.IDENTITY)
    @Id
    @Column(name = "fav_id")
    private int id;
    private LocalDate createdAt;
    private boolean status;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserModel user;
    @ManyToOne
    @JoinColumn(name = "RES_ID")
    private RestaurantModel restaurants;
    @ManyToOne
    @JoinColumn(name = "id")
    private MenuModel menu;
}
