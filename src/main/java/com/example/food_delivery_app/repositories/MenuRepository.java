package com.example.food_delivery_app.repositories;

import com.example.food_delivery_app.dto.repsonse.MenuResponseDto;
import com.example.food_delivery_app.models.MenuModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MenuRepository extends JpaRepository<MenuModel,Integer> {
    @Query(value = """
            select id menuId,menus.description , image,menus.name,price ,cate.cate_id cateId , cate."name" cateName
,menus.res_id resId,res.res_name resName,coupons.code discountCode,coupons.description discountDescription
,coupons.discount_type discountType,coupons.discount_value discountValue,coupons.min_order_amount minOrder,menus.rating
,CAST(
            CASE
                WHEN EXISTS (
                    SELECT 1
                    FROM favorites f2
                    WHERE f2.user_id = :user_id
                      AND f2.id = menus.id
                )
                THEN 1
                ELSE 0
            END AS INTEGER
        ) AS isFav
from menus inner join categories cate on cate.cate_id = menus.cate_id
inner join restaurants res on res.res_id = menus.res_id
left join coupon_assignments ca on ca.menu_id = menus.id
left join coupons on coupons.coupon_id = ca.coupon_id and coupons.max_usage >= coupons.used_count
and coupons.start_date <=  NOW() and coupons.end_date >= NOW()
where menus.res_id = :res_id ;
    """ ,nativeQuery = true)
    List<MenuResponseDto> getMenuByResId(@Param("user_id") Integer userId,@Param("res_id") Integer restaurantId);
    @Query(value = """
            select id menuId,menus.description , image,menus.name,price ,cate.cate_id cateId , cate."name" cateName
,menus.res_id resId,res.res_name resName,coupons.code discountCode,coupons.description discountDescription
,coupons.discount_type discountType,coupons.discount_value discountValue,coupons.min_order_amount minOrder,menus.rating
,CAST(
            CASE
                WHEN EXISTS (
                    SELECT 1
                    FROM favorites f2
                    WHERE f2.user_id = :user_id
                      AND f2.id = menus.id
                )
                THEN 1
                ELSE 0
            END AS INTEGER
        ) AS isFav
from menus inner join categories cate on cate.cate_id = menus.cate_id
inner join restaurants res on res.res_id = menus.res_id
left join coupon_assignments ca on ca.menu_id = menus.id
left join coupons on coupons.coupon_id = ca.coupon_id and coupons.max_usage >= coupons.used_count
and coupons.start_date <=  NOW() and coupons.end_date >= NOW()
where menus.created_at >= NOW() - INTERVAL '7 days' ;
    """ ,nativeQuery = true)
    List<MenuResponseDto> getNewMenu(@Param("user_id") int userId);
    @Query(value = """
         select id menuId,menus.description , image,menus.name,price ,cate.cate_id cateId , cate."name" cateName
            ,menus.res_id resId,res.res_name resName,coupons.code discountCode,coupons.description discountDescription
            ,coupons.discount_type discountType,coupons.discount_value discountValue,coupons.min_order_amount minOrder,menus.rating
            ,CAST(
                    CASE
                        WHEN EXISTS (
                            SELECT 1
                            FROM favorites f2
                            WHERE f2.user_id =:user_id
                              AND f2.id = menus.id
                        )
                        THEN 1
                        ELSE 0
                    END AS INTEGER
                ) AS isFav
            from menus inner join categories cate on cate.cate_id = menus.cate_id
            inner join restaurants res on res.res_id = menus.res_id
            inner join (select qty,item_code from (select sum(qty) qty,item_code from sale_detail group by item_code)
            temp order by qty limit 9 ) sd on sd.item_code = menus.name
            left join coupon_assignments ca on ca.menu_id = menus.id
            left join coupons on coupons.coupon_id = ca.coupon_id and coupons.max_usage >= coupons.used_count
            and coupons.start_date <=  NOW() and coupons.end_date >= NOW()
            where menus.res_id = :res_id;
        """,nativeQuery = true)
    Optional<MenuResponseDto> getPopularMenus(@Param("res_id") int resId , @Param("user_id") int userId);
}
