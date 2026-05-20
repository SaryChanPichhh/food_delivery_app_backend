 package com.example.food_delivery_app.dto.repsonse;


import java.time.LocalDateTime;

public interface DiscountResponseDto extends RestaurantResponseDto {

    String getCouponDesc();
    String getCouponCode();
    Double getDiscountValue();
    Integer getMaxUsage();
    Double getMinAmount();
    LocalDateTime getStartDate();
    LocalDateTime getEndDate();
    String getStatus();
}