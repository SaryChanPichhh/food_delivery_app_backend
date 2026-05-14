package com.example.food_delivery_app.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "free_delivery_assignments")
public class FreeDeliveryAssignmentModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private RestaurantModel restaurant;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private MenuModel menuItem;
    
    @Enumerated(EnumType.STRING)
    private AssignmentType assignmentType;
    
    @Enumerated(EnumType.STRING)
    private AssignmentStatus status;
    
    private Double minOrderAmount;
    
    private LocalDateTime startDate;
    
    private LocalDateTime endDate;
    
    private String notes;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    private LocalDateTime updatedAt;
    
    public FreeDeliveryAssignmentModel() {
        this.status = AssignmentStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    public enum AssignmentType {
        RESTAURANT,
        MENU_ITEM
    }
    
    public enum AssignmentStatus {
        ACTIVE,
        INACTIVE,
        EXPIRED
    }
}
