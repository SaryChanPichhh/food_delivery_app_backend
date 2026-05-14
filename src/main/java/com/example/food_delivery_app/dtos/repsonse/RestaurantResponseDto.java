package com.example.food_delivery_app.dtos.repsonse;

import lombok.Data;

public interface RestaurantResponseDto {
    String getResName();
    String getResId();
    String getImageUrl();
    Double getRating();
    Integer getQty();
    String getEstimateTime();
    Boolean getIsOpen();
    String getBasedCountry();
    String getCode();
    String getDiscountType();
    Double getValue();
    String getPopularDish();
    String getLatLng();
    Integer getIsFav();
}
