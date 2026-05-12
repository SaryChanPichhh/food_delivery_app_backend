package com.group_one.food_delivery_app.Interfaces;

import java.util.List;

import org.springframework.stereotype.Service;

import com.group_one.food_delivery_app.models.FavoritesModel;
@Service
public interface IFavoritesService extends IBasedService<FavoritesModel>  {
    List<FavoritesModel> findByUserIdAndRestaurantId(Long userId, Long restaurantId);
    List<FavoritesModel> findByUserIdAndId(Long userId, Long menuId);
    List<FavoritesModel> findByUserId(Long userId);
    FavoritesModel toggleFavorite(Long userId, Long resId, Long menuId);
}
