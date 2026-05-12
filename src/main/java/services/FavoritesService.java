package com.group_one.food_delivery_app.services;

import java.util.Dictionary;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.group_one.food_delivery_app.Interfaces.IFavoritesService;
import com.group_one.food_delivery_app.models.FavoritesModel;
import com.group_one.food_delivery_app.repositories.FavoritesRepository;
@Service
public class FavoritesService implements IFavoritesService {
    @Autowired
    private FavoritesRepository favoritesRepository;

    @Override
    public FavoritesModel AddData(FavoritesModel model) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'AddData'");
    }

    @Override
    public FavoritesModel UpdateData(FavoritesModel model) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'UpdateData'");
    }

    @Override
    public FavoritesModel Delete(FavoritesModel model) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'Delete'");
    }

    @Override
    public List<FavoritesModel> GetData() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'GetData'");
    }

    @Override
    public List<FavoritesModel> FindData(Dictionary<String, Object> model) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'FindData'");
    }

    @Override
    public List<FavoritesModel> findByUserIdAndRestaurantId(Long userId, Long restaurantId) {
        return favoritesRepository.findByUserIdAndRestaurantsResId(userId, restaurantId);
        // TODO Auto-generated method stub
    }

    @Override
    public List<FavoritesModel> findByUserIdAndId(Long userId, Long menuId) {
        // TODO Auto-generated method stub
        return favoritesRepository.findByUserIdAndId(userId, menuId);
    }

    @Override
    public List<FavoritesModel> findByUserId(Long userId) {
        // TODO Auto-generated method stub
        return favoritesRepository.findByUserId(userId);
    }

    @Override
    public FavoritesModel toggleFavorite(Long userId, Long resId, Long menuId) {
        if (menuId != null) {
            List<FavoritesModel> existing = favoritesRepository.findByUserIdAndId(userId, menuId);
            if (!existing.isEmpty()) {
                FavoritesModel fav = existing.get(0);
                favoritesRepository.delete(fav);
                return null; 
            } else {
                FavoritesModel fav = new FavoritesModel();
                com.group_one.food_delivery_app.models.UserModel user = new com.group_one.food_delivery_app.models.UserModel();
                user.setId(userId.intValue());
                fav.setUser(user);
                
                com.group_one.food_delivery_app.models.MenuModel menu = new com.group_one.food_delivery_app.models.MenuModel();
                menu.setId(menuId.intValue());
                fav.setMenu(menu);
                
                fav.setCreatedAt(java.time.LocalDate.now());
                fav.setStatus(true);
                return favoritesRepository.save(fav);
            }
        } else if (resId != null) {
            List<FavoritesModel> existing = favoritesRepository.findByUserIdAndRestaurantsResId(userId, resId);
            if (!existing.isEmpty()) {
                FavoritesModel fav = existing.get(0);
                favoritesRepository.delete(fav);
                return null;
            } else {
                FavoritesModel fav = new FavoritesModel();
                com.group_one.food_delivery_app.models.UserModel user = new com.group_one.food_delivery_app.models.UserModel();
                user.setId(userId.intValue());
                fav.setUser(user);
                
                com.group_one.food_delivery_app.models.RestaurantModel res = new com.group_one.food_delivery_app.models.RestaurantModel();
                res.setResId(resId.intValue());
                fav.setRestaurants(res);
                
                fav.setCreatedAt(java.time.LocalDate.now());
                fav.setStatus(true);
                return favoritesRepository.save(fav);
            }
        }
        return null;
    }
}
