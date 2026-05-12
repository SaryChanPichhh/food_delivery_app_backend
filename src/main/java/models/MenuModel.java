package com.group_one.food_delivery_app.models;

import jakarta.persistence.*;
import lombok.Data;

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
    private int quantity;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "RES_ID", nullable = false)
    private RestaurantModel restaurants;
    @ManyToOne
    @JoinColumn(name = "cate_id")
    private CategoryModel categories;
}
