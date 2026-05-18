package com.example.food_delivery_app.backend_admin.services;

import com.example.food_delivery_app.backend_admin.interfaces.IFavoritesService;
import com.example.food_delivery_app.models.FavoritesModel;
import com.example.food_delivery_app.models.MenuModel;
import com.example.food_delivery_app.models.RestaurantModel;
import com.example.food_delivery_app.models.UserModel;
import com.example.food_delivery_app.repositories.FavoritesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Dictionary;
import java.util.List;
@Service
public class AdminFavoritesService implements IFavoritesService {
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
                UserModel user = new UserModel();
                user.setId(userId.intValue());
                fav.setUser(user);
                
                MenuModel menu = new MenuModel();
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
                UserModel user = new UserModel();
                user.setId(userId.intValue());
                fav.setUser(user);
                
                RestaurantModel res = new RestaurantModel();
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
