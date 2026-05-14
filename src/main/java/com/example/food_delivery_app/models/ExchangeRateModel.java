package com.example.food_delivery_app.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "exchange_rates")
public class ExchangeRateModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String currencyCode; // e.g., "KHR"
    
    @Column(nullable = false)
    private String currencyName; // e.g., "Cambodian Riel"
    
    @Column(nullable = false)
    private Double rate; // e.g., 4100.0
    
    private String symbol; // e.g., "៛"
    
    @Column(nullable = false)
    private boolean defaultRate = false;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    public ExchangeRateModel() {
        this.updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    @PrePersist
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
