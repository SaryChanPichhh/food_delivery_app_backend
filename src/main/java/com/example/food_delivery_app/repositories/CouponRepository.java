package com.example.food_delivery_app.repositories;

 import com.example.food_delivery_app.dto.repsonse.MenuResponseDto;
 import com.example.food_delivery_app.dto.repsonse.DiscountResponeDto;
 import com.example.food_delivery_app.models.CouponModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<CouponModel, Long> {
    
    // Find coupon by code
    Optional<CouponModel> findByCode(String code);
    
    // Find active coupons
    @Query("SELECT c FROM CouponModel c WHERE c.status = 'ACTIVE' AND c.startDate <= :currentDate AND c.endDate >= :currentDate")
    List<CouponModel> findActiveCoupons(@Param("currentDate") LocalDateTime currentDate);
    
    // Find coupons by status
    List<CouponModel> findByStatus(CouponModel.CouponStatus status);
    
    // Find expired coupons
    @Query("SELECT c FROM CouponModel c WHERE c.endDate < :currentDate AND c.status != 'EXPIRED'")
    List<CouponModel> findExpiredCoupons(@Param("currentDate") LocalDateTime currentDate);
    
    // Check if code exists (excluding specific ID for updates)
    @Query("SELECT COUNT(c) > 0 FROM CouponModel c WHERE c.code = :code AND c.couponId != :excludeId")
    boolean existsByCodeAndCouponIdNot(@Param("code") String code, @Param("excludeId") Long excludeId);
    
    // Check if code exists (for create)
    boolean existsByCode(String code);
    
    // Search coupons by code or description
    @Query("SELECT c FROM CouponModel c WHERE c.code LIKE %:keyword% OR c.description LIKE %:keyword%")
    List<CouponModel> searchCoupons(@Param("keyword") String keyword);
    
    // Find coupons with usage limit reached
    @Query("SELECT c FROM CouponModel c WHERE c.maxUsage IS NOT NULL AND c.usedCount >= c.maxUsage AND c.status = 'ACTIVE'")
    List<CouponModel> findCouponsWithUsageLimitReached();

    @Query("SELECT c FROM CouponModel c JOIN CouponAssignmentModel ca ON c.couponId = ca.coupon.couponId " +
           "WHERE ca.restaurant.resId = :resId AND c.status = 'ACTIVE' " +
           "AND c.startDate <= :now AND c.endDate >= :now")
    Optional<CouponModel> findActiveCouponByRestaurantId(@Param("resId") Integer resId, @Param("now") LocalDateTime now);

    @Query(value = """
            SELECT 
        r.res_name AS resName,
        r.res_id AS resId,
        r.image_url AS imageUrl,
        r.rating AS rating,
        COALESCE(s.qty,0) AS qty,
        COALESCE(r.avg_estimate_time,'10-25 min') AS estimateTime,
        r.is_open AS isOpen,
        r.based_country AS basedCountry,
		(
    CASE 
        WHEN EXISTS (
            SELECT 1 
            FROM favorites f 
            WHERE f.user_id = :userId 
              AND f.res_id = r.res_id
        ) 
        THEN 1 
        ELSE 0 
    END::INTEGER
) AS isFav,
       (
    SELECT item_desc from sale_detail INNER JOIN restaurants on restaurants.res_id = sale_detail.res_id where sale_detail.res_id = r.res_id
		GROUP BY sale_detail.res_id,item_desc ORDER BY SUM(qty) DESC LIMIT 1
) AS popularDish,coupons.description as couponDesc,coupons.code as couponCode,coupons.discount_type as discountType
,coupons.discount_value as discountValue,coupons.max_usage as maxUsage,coupons.min_order_amount as minAmount,
coupons.start_date as startDate,coupons.end_date as endDate,coupons.status as status
    FROM restaurants r
    INNER JOIN (
        SELECT ca.restaurant_id, MIN(ca.coupon_id) as coupon_id 
        FROM coupon_assignments ca
        JOIN coupons c ON c.coupon_id = ca.coupon_id
        WHERE c.status = 'ACTIVE' 
          AND c.start_date <= :now 
          AND c.end_date >= :now
        GROUP BY ca.restaurant_id
    ) ca on r.res_id = ca.restaurant_id
    INNER JOIN coupons ON coupons.coupon_id = ca.coupon_id 
    LEFT JOIN (SELECT COUNT(*) qty,res_id from sale_detail GROUP BY res_id) s 
        ON r.res_id = s.res_id
    WHERE coupons.status = 'ACTIVE' 
      AND coupons.start_date <= :now 
      AND coupons.end_date >= :now
      AND (coupons.max_usage IS NULL OR COALESCE(coupons.used_count, 0) < coupons.max_usage)
            """,nativeQuery=true)
    List<DiscountResponeDto> getDiscountInfo(@Param("userId") int userId, @Param("now") LocalDateTime now);


    @Query(value = """

            select id menuId,menus.description , image,menus.name,price ,cate.cate_id cateId , cate."name" cateName
           ,menus.res_id resId,res.res_name resName,coupons.code discountCode,coupons.description discountDescription
           ,coupons.discount_type discountType,coupons.discount_value discountValue,coupons.min_order_amount minOrder,menus.rating
           ,CAST(
                       CASE\s
                           WHEN EXISTS (
                               SELECT 1\s
                               FROM favorites f2\s
                               WHERE f2.user_id = 1
                                 AND f2.id = menus.id
                           )\s
                           THEN 1\s
                           ELSE 0\s
                       END AS INTEGER
                   ) AS isFav
           from menus inner join categories cate on cate.cate_id = menus.cate_id
           inner join restaurants res on res.res_id = menus.res_id
           inner join coupon_assignments ca on ca.menu_id = menus.id
           inner join coupons on coupons.coupon_id = ca.coupon_id\s
           where coupons.max_usage >= coupons.used_count
           and coupons.start_date <=  NOW() and coupons.end_date >= NOW() and coupons.status = 'ACTIVE' ;
            """,nativeQuery=true)
    List<MenuResponseDto> getDiscountOnMenuInfo(@Param("userId") int userId, @Param("now") LocalDateTime now);
}
