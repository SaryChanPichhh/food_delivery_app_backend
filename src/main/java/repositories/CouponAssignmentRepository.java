package com.group_one.food_delivery_app.repositories;

import com.group_one.food_delivery_app.models.CouponAssignmentModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CouponAssignmentRepository extends JpaRepository<CouponAssignmentModel, Long> {
    
    // Find assignments by coupon
    List<CouponAssignmentModel> findByCouponCouponId(Long couponId);
    
    // Find assignments by restaurant
    List<CouponAssignmentModel> findByRestaurantResId(Integer restaurantId);
    
    // Find assignments by menu item
    List<CouponAssignmentModel> findByMenuItemId(Integer menuItemId);
    
    // Find active assignments
    @Query("SELECT ca FROM CouponAssignmentModel ca WHERE ca.status = 'ACTIVE'")
    List<CouponAssignmentModel> findActiveAssignments();
    
    // Find assignments by status
    List<CouponAssignmentModel> findByStatus(CouponAssignmentModel.AssignmentStatus status);
    
    // Find assignments by assignment type
    List<CouponAssignmentModel> findByAssignmentType(CouponAssignmentModel.AssignmentType assignmentType);
    
    // Search assignments by notes or target name
    @Query("SELECT ca FROM CouponAssignmentModel ca WHERE " +
           "(LOWER(ca.notes) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(ca.restaurant.resName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(ca.menuItem.name) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<CouponAssignmentModel> searchAssignments(@Param("keyword") String keyword);
    
    // Find assignments for specific coupon and restaurant
    @Query("SELECT ca FROM CouponAssignmentModel ca WHERE ca.coupon.couponId = :couponId AND ca.restaurant.resId = :restaurantId")
    Optional<CouponAssignmentModel> findByCouponAndRestaurant(@Param("couponId") Long couponId, @Param("restaurantId") Integer restaurantId);
    
    // Find assignments for specific coupon and menu item
    @Query("SELECT ca FROM CouponAssignmentModel ca WHERE ca.coupon.couponId = :couponId AND ca.menuItem.id = :menuItemId")
    Optional<CouponAssignmentModel> findByCouponAndMenuItem(@Param("couponId") Long couponId, @Param("menuItemId") Integer menuItemId);
    
//    // Check if assignment exists
//    boolean existsByCouponAndRestaurant(Long couponId, Integer restaurantId);
//    boolean existsByCouponAndMenuItem(Long couponId, Integer menuItemId);
//
    // Find expired assignments
    @Query("SELECT ca FROM CouponAssignmentModel ca WHERE ca.status = 'EXPIRED'")
    List<CouponAssignmentModel> findExpiredAssignments();
    
    // Count assignments by coupon
    long countByCouponCouponId(Long couponId);
    
    // Count assignments by restaurant
    long countByRestaurantResId(Integer restaurantId);
}
