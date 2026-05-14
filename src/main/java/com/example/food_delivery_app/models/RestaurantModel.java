package com.example.food_delivery_app.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
@Data
@Entity
@Table(name = "restaurants")
public class RestaurantModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "RES_ID")
    private int resId;
    private String resName;
    private String description;
    private String address;
    private LocalTime openTime;
    private LocalTime closeTime;
    private String latLng;
    private LocalDate createdAt;
    private double rating;
    private String avgEstimateTime;
    private String imageUrl;
    private boolean isOpen;
    private String basedCountry;
    private double commissionRate; // Commission percentage (e.g., 10.0 for 10%)
}
