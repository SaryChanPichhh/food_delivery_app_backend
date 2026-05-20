package com.example.food_delivery_app.backend_user.services;

import com.example.food_delivery_app.backend_user.interfaces.IRestaurantService;
import com.example.food_delivery_app.dto.repsonse.RestaurantResponseDto;
import com.example.food_delivery_app.models.RestaurantModel;
import com.example.food_delivery_app.repositories.RestaurantRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;
@Service
@AllArgsConstructor
public class RestaurantService implements IRestaurantService {
    private final RestaurantRepository restaurantRepository;
    @Override
    public RestaurantModel AddData(RestaurantModel model) {
        return null;
    }

    @Override
    public RestaurantModel UpdateData(RestaurantModel model) {
        return null;
    }

    @Override
    public RestaurantModel Delete(RestaurantModel model) {
        return null;
    }

    @Override
    public List<RestaurantModel> GetData() {
        return restaurantRepository.findAll();
    }

    @Override
    public List<RestaurantModel> FindData(Dictionary<String, Object> model) {
        return List.of();
    }

    @Override
    public List<RestaurantResponseDto> GetPopularRestaurant(int userId) {
        var data = restaurantRepository.GetPopularRestaurant(userId);
        return data;
    }

    @Override
    public List<RestaurantResponseDto> GetNewRestaurant(int userId) {
        var data = restaurantRepository.GetNewRestaurants(userId);
        return data.orElse(new ArrayList<>());
    }

    @Override
    public List<RestaurantModel> GetResWhichGotDiscount() {
        return List.of();
    }
}
