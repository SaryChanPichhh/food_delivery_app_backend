package com.example.food_delivery_app.backend_admin.interfaces;

import com.example.food_delivery_app.dto.repsonse.MenuResponseDto;
import com.example.food_delivery_app.models.MenuModel;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface IMenuService extends IBasedService<MenuModel> {
    List<MenuResponseDto> getMenuItemsByRestaurant(Integer userId, Integer restaurantId);
    MenuModel findById(Integer id);
}
