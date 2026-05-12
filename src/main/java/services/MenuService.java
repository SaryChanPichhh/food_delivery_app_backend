package com.group_one.food_delivery_app.services;

import com.group_one.food_delivery_app.Interfaces.IMenuService;
import com.group_one.food_delivery_app.models.MenuModel;
import com.group_one.food_delivery_app.repositories.MenuRepository;
import org.springframework.stereotype.Service;

import java.util.Dictionary;
import java.util.List;
@Service
public class MenuService implements IMenuService {
    private final MenuRepository menuRepository;

    public MenuService(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    @Override
    public MenuModel AddData(MenuModel model) {
        return null;
    }

    @Override
    public MenuModel UpdateData(MenuModel model) {
        return null;
    }

    @Override
    public MenuModel Delete(MenuModel model) {
        return null;
    }

    @Override
    public List<MenuModel> GetData() {
        return menuRepository.findAll();
    }

    @Override
    public List<MenuModel> FindData(Dictionary<String, Object> model) {
        return List.of();
    }

    public List<MenuModel> getMenuItemsByRestaurant(Integer restaurantId) {
        return menuRepository.findByRestaurantsResId(restaurantId);
    }

    @Override
    public MenuModel findById(Integer id) {
        return menuRepository.findById(id).orElse(null);
    }
}
