package com.example.food_delivery_app.backend_user.interfaces;

import com.example.food_delivery_app.dto.repsonse.MenuResponseDto;
import com.example.food_delivery_app.models.MenuModel;
import org.springframework.stereotype.Service;

import java.util.List;

public interface IMenuService extends IBasedService<MenuModel> {
    List<MenuModel> getMenuItemsByRestaurant(Integer restaurantId);
    MenuModel findById(Integer id);
    List<MenuResponseDto> getNewMenus(int userId);
    List<MenuResponseDto> getMenuByResId(Integer userId,Integer resId);
    List<MenuResponseDto> getPopularMenus(int userId,int resId);
}
