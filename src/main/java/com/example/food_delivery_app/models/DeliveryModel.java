package com.example.food_delivery_app.models;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "deliveries")
@Data
public class DeliveryModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "del_id")
    private Long id;
    
    private String name;
    private String email;
    private String phone;
    private String address;
    private boolean status;
    private String imageUrl;
    private String nationalId;
    private String driverLicense;
    private String nationalIdUrl;
    private String driverLicenseUrl;
    private int rating;
    @Column(nullable = true)
    private String city;
    @Column(nullable = true)
    private String state;
    @Column(nullable = true)
    private String zip;
    @Column(nullable = true)
    private String country;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserModel users;
}