package com.example.food_delivery_app.backend_user.interfaces;

import com.example.food_delivery_app.models.FavoritesModel;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface IFavoritesService extends IBasedService<FavoritesModel> {
    List<FavoritesModel> findByUserIdAndRestaurantId(Long userId, Long restaurantId);
    List<FavoritesModel> findByUserIdAndId(Long userId, Long menuId);
    List<FavoritesModel> findByUserId(Long userId);
    FavoritesModel toggleFavorite(Long userId, Long resId, Long menuId);
}
