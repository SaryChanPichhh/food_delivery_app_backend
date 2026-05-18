package com.example.food_delivery_app.dto.repsonse;

import lombok.Data;
import org.w3c.dom.stylesheets.LinkStyle;

import java.util.List;
@Data
public class CartRestaurantResponseDto {
    private Integer resId;
    private String resName;
    private String resImage;
    private String resDesc;
    private Boolean isOpen;
    private String address;
    private String avgEstimateTime;
    private Double subTotal;
    private Double totalDiscount;
    private Double totalAfterDiscount;
    List<CartItemDto> items;

}
