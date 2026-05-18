package com.example.food_delivery_app.dto.repsonse;

public interface CartItemResponseDto {
    Integer getHeaderId();
    Integer getDetailId();
    Integer getResId();
    String getItemCode();
    String getMenuDescription();
    String getMenuImage();
    Integer getQty();
    Double getSalePrice();
    Double getTotal();
    Double getDiscountValue();
    Double getDiscountType();
    String getResName();
    String getResImage();
    String getResDesc();
    Boolean getIsOpen();
    String getAddress();
    String getAvgEstimateTime();
}