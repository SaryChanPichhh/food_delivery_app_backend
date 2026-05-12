package com.group_one.food_delivery_app.repositories;

import com.group_one.food_delivery_app.models.MenuModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<MenuModel,Integer> {
    List<MenuModel> findByRestaurantsResId(Integer restaurantId);
}
