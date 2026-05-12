package com.group_one.food_delivery_app.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.group_one.food_delivery_app.dtos.repsonse.CartResponeDto;
import com.group_one.food_delivery_app.models.SaleDetailModel;
import com.group_one.food_delivery_app.utils.enums.InvoiceType;
@Repository
public interface CartRepository extends JpaRepository<SaleDetailModel, Integer> {
    @Query(value = """
            SELECT 
        sale_header.header_id AS headerId, 
        sale_detail.detail_id AS detailId, 
        item_code AS itemCode, 
        menus.name AS menuDescription, 
        menus.image AS menuImage, 
        qty, 
        sale_price AS salePrice, 
        sale_detail.total AS total, 
        discount_value AS discountValue, 
        restaurants.res_id AS resId, 
        res_name AS resName,
        restaurants.image_url AS resImage,
				restaurants.description as resDesc,
        restaurants.is_open AS isOpen, 
        restaurants.address AS address, 
        restaurants.avg_estimate_time AS avgEstimateTime 
        FROM sale_header 
        INNER JOIN sale_detail on sale_header.header_id = sale_detail.header_id 
        LEFT JOIN coupons on coupons.coupon_id = sale_detail.coupon_id 
        INNER JOIN restaurants on restaurants.res_id = sale_detail.res_id 
        INNER JOIN users ON users.user_id = sale_header.user_id 
        INNER JOIN menus on item_code = menus.id 
        WHERE invoice_type = 'ORDER' AND users.user_id = :userId AND sale_header.status = 1
            """,
        nativeQuery = true)
        List<CartResponeDto> GetCartByUserId(
                @Param("invoiceType") InvoiceType invoiceType,
                @Param("userId") int userId
);
        
        SaleDetailModel findBySaleHeader_IdAndItemCode(int headerId, String itemCode);
        List<SaleDetailModel> findAllBySaleHeader_Id(int headerId);
}
