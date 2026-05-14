package com.example.food_delivery_app.models;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "coupon_assignments")
public class CouponAssignmentModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long assignmentId;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id", nullable = false)
    private CouponModel coupon;
    
    // Target can be either a restaurant or a menu item
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "restaurant_id")
    private RestaurantModel restaurant;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_id")
    private MenuModel menuItem;
    
    @Enumerated(EnumType.STRING)
    private AssignmentType assignmentType;
    
    private String notes;
    
    @Enumerated(EnumType.STRING)
    private AssignmentStatus status;
    
    @Column(nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    // Constructors
    public CouponAssignmentModel() {
        this.status = AssignmentStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Enums
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
