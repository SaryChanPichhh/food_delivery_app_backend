package com.group_one.food_delivery_app.repositories;

import com.group_one.food_delivery_app.models.FreeDeliveryAssignmentModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FreeDeliveryAssignmentRepository extends JpaRepository<FreeDeliveryAssignmentModel, Long> {
    
    @Query("SELECT f FROM FreeDeliveryAssignmentModel f WHERE " +
           "(:keyword IS NULL OR :keyword = '' OR " +
           "f.restaurant.resName LIKE %:keyword% OR " +
           "f.menuItem.name LIKE %:keyword% OR " +
           "f.notes LIKE %:keyword%)")
    Page<FreeDeliveryAssignmentModel> findAllByKeyword(@Param("keyword") String keyword, Pageable pageable);

    List<FreeDeliveryAssignmentModel> findByRestaurantResId(Integer restaurantId);

    List<FreeDeliveryAssignmentModel> findByMenuItemId(Integer menuItemId);
}
