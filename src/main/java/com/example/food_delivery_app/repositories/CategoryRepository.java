package com.example.food_delivery_app.repositories;

import com.example.food_delivery_app.dtos.repsonse.RestaurantResponseDto;
import com.example.food_delivery_app.models.CategoryModel;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepository extends JpaRepository<CategoryModel, Integer> {
    @Query(value = """
                    SELECT
                    r.res_name AS resName,
                    r.res_id AS resId,
                    r.image_url AS imageUrl,
                    r.rating AS rating,
                    IFNULL(s.qty,0) AS qty,
                    IFNULL(r.avg_estimate_time,'10-25 min') AS estimateTime,
                    r.is_open AS isOpen,
                    r.based_country AS basedCountry,
                    d.code AS code,
                    IFNULL(d.discount_value,0) value,
                    d.discount_type AS discountType,
                    d.start_date AS startDate,
                    d.end_date AS endDate,
                    d.status AS status,
                    r.lat_lng AS latLng,
            		CAST(
                CASE
                    WHEN EXISTS (
                        SELECT 1
                        FROM favorites f
                        WHERE f.user_id = :userId
                          AND f.res_id = r.res_id
                    )
                    THEN 1
                    ELSE 0
                END AS UNSIGNED
            ) AS isFav,
                   (
                SELECT item_desc from sale_detail INNER JOIN restaurants on restaurants.res_id = sale_detail.res_id where sale_detail.res_id = r.res_id
            		GROUP BY sale_detail.res_id,item_desc ORDER BY SUM(qty) DESC LIMIT 1
            ) AS popularDish
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
                WHERE EXISTS (SELECT 1 FROM menus m WHERE m.RES_ID = r.res_id AND m.cate_id = :cateId);
                    """, nativeQuery = true)
    List<RestaurantResponseDto> GetCategoryDetail(@Param("cateId") int cateId, @Param("userId") int userId);
}
