package com.example.food_delivery_app.dto.repsonse;

import lombok.Data;

@Data
public class FavoritesResponseDto {
    private Long id;
    private Long userId;
    private Long resId;
    private Long menuId;
}
