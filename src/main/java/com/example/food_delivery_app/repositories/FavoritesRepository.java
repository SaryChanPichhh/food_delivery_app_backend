package com.example.food_delivery_app.repositories;

import com.example.food_delivery_app.models.FavoritesModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface FavoritesRepository extends JpaRepository<FavoritesModel, Integer> {
    List<FavoritesModel> findByUserIdAndId(Long userId, Long menuId);
// ប្តូរពី findByUserIdAndRestaurantsId ទៅជា៖
List<FavoritesModel> findByUserIdAndRestaurantsResId(Long userId, Long resId);
// @Query(value = "SELECT * FROM favorites WHERE user_id = ?1",nativeQuery = true)
List<FavoritesModel> findByUserId(Long userId);
}
