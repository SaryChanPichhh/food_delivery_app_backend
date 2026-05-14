package com.example.food_delivery_app.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Table(name = "faqs")
@Data
public class FaqModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "TEXT")
    private String question;

    @Column(columnDefinition = "TEXT")
    private String answer;

    private int sortOrder;
    private boolean active = true;
    
    private LocalDateTime createdAt = LocalDateTime.now();
}
