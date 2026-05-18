package com.example.food_delivery_app.dto.repsonse;

import lombok.Data;

@Data
public class CartItemDto
{
    private Integer headerId;
    private Integer detailId;
    private String itemCode;
    private String menuDescription;
    private String menuImage;
    private Integer qty;
    private Double salePrice;
    private Double total;
    private Double discountValue;
    private Double savingValue;
    private Double totalAfterDiscount;
}
