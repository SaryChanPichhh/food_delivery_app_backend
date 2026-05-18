package com.example.food_delivery_app.backend_user.interfaces;

import com.example.food_delivery_app.dto.repsonse.CartItemResponseDto;
import com.example.food_delivery_app.dto.repsonse.CartRestaurantResponseDto;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface ICartService  {
    List<CartRestaurantResponseDto> GetCartByUserId(int userId);
}
