package com.example.food_delivery_app.dto.repsonse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public interface MenuResponseDto {
    Integer getMenuId();
    String getName();
    Double getPrice();
    String getDescription();
    String getImage();
    Double getRating();

    Integer getResId();
    String getResName();

    Integer getCateId();
    String getCateName();

    String getDiscountCode();
    String getDiscountDescription();
    String getDiscountType();
    Double getDiscountValue();

    Double getMinOrder();
    Integer getIsFav();
}
