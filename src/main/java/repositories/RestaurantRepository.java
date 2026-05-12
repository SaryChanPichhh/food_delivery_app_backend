package com.group_one.food_delivery_app.repositories;

import com.group_one.food_delivery_app.models.RestaurantModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface RestaurantRepository extends JpaRepository<RestaurantModel,Integer> {

}
