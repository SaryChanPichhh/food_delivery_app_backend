package com.example.food_delivery_app.dto.repsonse;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
}
