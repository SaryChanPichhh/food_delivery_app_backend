package com.group_one.food_delivery_app.repositories;

import com.group_one.food_delivery_app.models.ReviewModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ReviewRepository extends JpaRepository<ReviewModel, Long> {
    List<ReviewModel> findByRestaurantResIdOrderByCreatedAtDesc(int resId);
    
    // Check if user has already reviewed this restaurant (optional)
    boolean existsByUserIdAndRestaurantResId(int userId, int resId);
}
