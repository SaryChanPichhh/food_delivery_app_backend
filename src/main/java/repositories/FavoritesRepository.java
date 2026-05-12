package com.group_one.food_delivery_app.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.group_one.food_delivery_app.models.FavoritesModel;
@Repository
public interface FavoritesRepository extends JpaRepository<FavoritesModel, Integer> {
    List<FavoritesModel> findByUserIdAndId(Long userId, Long menuId);
// ប្តូរពី findByUserIdAndRestaurantsId ទៅជា៖
List<FavoritesModel> findByUserIdAndRestaurantsResId(Long userId, Long resId);
// @Query(value = "SELECT * FROM favorites WHERE user_id = ?1",nativeQuery = true)
List<FavoritesModel> findByUserId(Long userId);
}
