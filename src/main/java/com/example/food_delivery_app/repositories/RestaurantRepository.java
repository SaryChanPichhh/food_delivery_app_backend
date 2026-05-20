package com.example.food_delivery_app.repositories;

import com.example.food_delivery_app.dto.repsonse.RestaurantResponseDto;
import com.example.food_delivery_app.models.RestaurantModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RestaurantRepository extends JpaRepository<RestaurantModel,Integer> {

//    List<RestaurantModel> GetResWhichGotDiscount();
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
        d.code AS code,
        COALESCE(d.discount_value,0) value,
        d.discount_type AS discountType,
        d.start_date AS startDate,
        d.end_date AS endDate,
        d.status AS status,
        r.lat_lng AS latLng,
        (
            SELECT item_desc from sale_detail INNER JOIN restaurants on restaurants.res_id = sale_detail.res_id where sale_detail.res_id = r.res_id
            GROUP BY sale_detail.res_id,item_desc ORDER BY SUM(qty) DESC LIMIT 1
        ) AS popularDish,
        CAST(
            CASE 
                WHEN EXISTS (
                    SELECT 1 
                    FROM favorites f2 
                    WHERE f2.user_id = :userId 
                      AND f2.res_id = r.res_id
                ) 
                THEN 1 
                ELSE 0 
            END AS INTEGER
        ) AS isFav
    FROM restaurants r
    LEFT JOIN (SELECT COUNT(*) qty,res_id from sale_detail GROUP BY res_id) s 
        ON r.res_id = s.res_id
    LEFT JOIN (
        SELECT ca.restaurant_id, MIN(ca.coupon_id) as coupon_id 
        FROM coupon_assignments ca
        JOIN coupons c ON c.coupon_id = ca.coupon_id
        WHERE c.status = 'ACTIVE' 
          AND c.start_date <= NOW() 
          AND c.end_date >= NOW()
        GROUP BY ca.restaurant_id
    ) unique_ca ON r.res_id = unique_ca.restaurant_id
    LEFT JOIN coupons d ON d.coupon_id = unique_ca.coupon_id 
        AND d.status = 'ACTIVE' 
        AND d.start_date <= NOW() 
        AND d.end_date >= NOW()
""",nativeQuery = true)
    List<RestaurantResponseDto> GetPopularRestaurant(@Param("userId") int userId);

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
        d.code AS code,
        COALESCE(d.discount_value,0) value,
        d.discount_type AS discountType,
        d.start_date AS startDate,
        d.end_date AS endDate,
        d.status AS status,
        r.lat_lng AS latLng,
        (
            SELECT item_desc from sale_detail INNER JOIN restaurants on restaurants.res_id = sale_detail.res_id where sale_detail.res_id = r.res_id
            GROUP BY sale_detail.res_id,item_desc ORDER BY SUM(qty) DESC LIMIT 1
        ) AS popularDish,
        CAST(
            CASE 
                WHEN EXISTS (
                    SELECT 1 
                    FROM favorites f2 
                    WHERE f2.user_id = :userId 
                      AND f2.res_id = r.res_id
                ) 
                THEN 1 
                ELSE 0 
            END AS INTEGER
        ) AS isFav
    FROM restaurants r
    LEFT JOIN (SELECT COUNT(*) qty,res_id from sale_detail GROUP BY res_id) s 
        ON r.res_id = s.res_id
    LEFT JOIN (
        SELECT ca.restaurant_id, MIN(ca.coupon_id) as coupon_id 
        FROM coupon_assignments ca
        JOIN coupons c ON c.coupon_id = ca.coupon_id
        WHERE c.status = 'ACTIVE' 
          AND c.start_date <= NOW() 
          AND c.end_date >= NOW()
        GROUP BY ca.restaurant_id
    ) unique_ca ON r.res_id = unique_ca.restaurant_id
    LEFT JOIN coupons d ON d.coupon_id = unique_ca.coupon_id 
        AND d.status = 'ACTIVE' 
        AND d.start_date <= NOW() 
        AND d.end_date >= NOW()
    WHERE r.created_at >= NOW() - INTERVAL '7 days' ;
""",nativeQuery = true)
    Optional<List<RestaurantResponseDto>> GetNewRestaurants(@Param("userId") int userId);
}
