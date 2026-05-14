package com.example.food_delivery_app.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "users")
public class UserModel {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "user_id")
    private int id;
    private String userName;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private String password;
    private String role;
    @Column(nullable = true)
    private String profileImage;
    @Column(nullable = false, columnDefinition = "boolean default true")
    private boolean isActive = true;
    private LocalDate createdAt;
}
