package com.example.food_delivery_app.backend_user.services;

import com.example.food_delivery_app.backend_user.interfaces.IMenuService;
import com.example.food_delivery_app.dto.repsonse.MenuResponseDto;
import com.example.food_delivery_app.models.MenuModel;
import com.example.food_delivery_app.repositories.MenuRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.List;



@Slf4j
@Service public class MenuService implements IMenuService {
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
        return List.of();
    }
    @Override
    public MenuModel findById(Integer id) {
        return menuRepository.findById(id).orElse(null);
    }
    @Override
    public List<MenuResponseDto> getNewMenus(int userId) {
        log.info("get new menu by this user ={}",userId);
        return menuRepository.getNewMenu(userId);
    }
    @Cacheable(value = "getMenuByResId",key ="#userId + '-' + #resId")
    @Override
    public List<MenuResponseDto> getMenuByResId(Integer userId, Integer resId) {
        return menuRepository.getMenuByResId(userId, resId);
    }

    @Override
    public List<MenuResponseDto> getPopularMenus(int userId,int resId) {
        var data = menuRepository.getPopularMenus(userId,resId);
        return data.stream().toList();
    }
}
