package com.example.food_delivery_app.backend_user.interfaces;

import com.example.food_delivery_app.dto.repsonse.RestaurantResponseDto;
import com.example.food_delivery_app.models.RestaurantModel;
import org.springframework.stereotype.Service;

import java.util.List;

public interface IRestaurantService extends IBasedService<RestaurantModel> {
    List<RestaurantResponseDto> GetPopularRestaurant(int userId);
    List<RestaurantModel> GetResWhichGotDiscount();
}
