package com.example.food_delivery_app.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "menus")
public class MenuModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private double price;
    private String description;
    private String image;
    private double rating;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RES_ID", nullable = false)
    private RestaurantModel restaurants;
    @ManyToOne
    @JoinColumn(name = "cate_id")
    private CategoryModel categories;
    private LocalDate createdAt;
    private LocalDate createdBy;
    private LocalDate updatedAt;
    private LocalDate updatedBy;
}
