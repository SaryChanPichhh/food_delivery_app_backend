package com.example.food_delivery_app.backend_admin.services;

import com.example.food_delivery_app.backend_admin.interfaces.IMenuService;
import com.example.food_delivery_app.dto.repsonse.MenuResponseDto;
import com.example.food_delivery_app.models.MenuModel;
import com.example.food_delivery_app.repositories.MenuRepository;
import org.springframework.stereotype.Service;

import java.util.Dictionary;
import java.util.List;
@Service
public class AdminMenuService implements IMenuService {
    private final MenuRepository menuRepository;

    public AdminMenuService(MenuRepository menuRepository) {
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

    public List<MenuResponseDto> getMenuItemsByRestaurant(Integer userId, Integer restaurantId) {
        return menuRepository.getMenuByResId(userId,restaurantId);
    }

    @Override
    public MenuModel findById(Integer id) {
        return menuRepository.findById(id).orElse(null);
    }
}
