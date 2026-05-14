package com.example.food_delivery_app.repositories;

import com.example.food_delivery_app.models.RestaurantModel;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RestaurantRepository extends JpaRepository<RestaurantModel,Integer> {

}
