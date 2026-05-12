package com.group_one.food_delivery_app.Interfaces;

import com.group_one.food_delivery_app.models.MenuModel;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public interface IMenuService extends IBasedService<MenuModel>{
    List<MenuModel> getMenuItemsByRestaurant(Integer restaurantId);
    MenuModel findById(Integer id);
}
