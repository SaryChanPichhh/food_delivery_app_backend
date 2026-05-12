package com.group_one.food_delivery_app.services;

import com.group_one.food_delivery_app.Interfaces.IRestaurantService;
import com.group_one.food_delivery_app.models.RestaurantModel;
import com.group_one.food_delivery_app.repositories.RestaurantRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

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
}
