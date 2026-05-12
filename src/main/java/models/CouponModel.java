package com.group_one.food_delivery_app.models;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
public class CouponModel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long couponId;
    
    @Column(nullable = false, unique = true)
    private String code;
    
    @Column(nullable = false)
    private String description;
    
    @Enumerated(EnumType.STRING)
    private DiscountType discountType;
    
    @Column(nullable = false)
    private Double discountValue;
    
    @Column(nullable = false)
    private Double minOrderAmount;
    
    private Integer maxUsage;
    
    private Integer usedCount;
    
    @Column(nullable = false)
    private LocalDateTime startDate;
    
    @Column(nullable = false)
    private LocalDateTime endDate;
    
    @Enumerated(EnumType.STRING)
    private CouponStatus status;
    
    @Column(nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    // Helper methods for date management
    public boolean isExpired() {
        return endDate != null && LocalDateTime.now().isAfter(endDate);
    }
    
    public boolean isNotStartedYet() {
        return startDate != null && LocalDateTime.now().isBefore(startDate);
    }
    
    public boolean isCurrentlyActive() {
        LocalDateTime now = LocalDateTime.now();
        return status == CouponStatus.ACTIVE && 
               (startDate == null || !now.isBefore(startDate)) && 
               (endDate == null || !now.isAfter(endDate));
    }
    
    public void updateStatusBasedOnDate() {
        if (isExpired()) {
            this.status = CouponStatus.EXPIRED;
        } else if (isNotStartedYet() && this.status == CouponStatus.EXPIRED) {
            // If it was expired but now dates changed to future, reset to ACTIVE or keep as is?
            // Usually, if dates are changed, the user would manually set status too, 
            // but for safety, let's just focus on auto-expiring.
            this.status = CouponStatus.ACTIVE;
        }
    }
    
    // Constructors
    public CouponModel() {
        this.status = CouponStatus.ACTIVE;
        this.usedCount = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getCouponId() {
        return couponId;
    }
    
    public void setCouponId(Long couponId) {
        this.couponId = couponId;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public DiscountType getDiscountType() {
        return discountType;
    }
    
    public void setDiscountType(DiscountType discountType) {
        this.discountType = discountType;
    }
    
    public Double getDiscountValue() {
        return discountValue;
    }
    
    public void setDiscountValue(Double discountValue) {
        this.discountValue = discountValue;
    }
    
    public Double getMinOrderAmount() {
        return minOrderAmount;
    }
    
    public void setMinOrderAmount(Double minOrderAmount) {
        this.minOrderAmount = minOrderAmount;
    }
    
    public Integer getMaxUsage() {
        return maxUsage;
    }
    
    public void setMaxUsage(Integer maxUsage) {
        this.maxUsage = maxUsage;
    }
    
    public Integer getUsedCount() {
        return usedCount;
    }
    
    public void setUsedCount(Integer usedCount) {
        this.usedCount = usedCount;
    }
    
    public LocalDateTime getStartDate() {
        return startDate;
    }
    
    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }
    
    public LocalDateTime getEndDate() {
        return endDate;
    }
    
    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }
    
    public CouponStatus getStatus() {
        return status;
    }
    
    public void setStatus(CouponStatus status) {
        this.status = status;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // Enums
    public enum DiscountType {
        PERCENTAGE,
        FIXED_AMOUNT
    }
    
    public enum CouponStatus {
        ACTIVE,
        INACTIVE,
        EXPIRED
    }
}
