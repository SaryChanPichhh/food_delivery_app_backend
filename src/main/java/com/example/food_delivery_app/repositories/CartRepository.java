package com.example.food_delivery_app.repositories;

import com.example.food_delivery_app.dto.repsonse.CartItemResponseDto;
import com.example.food_delivery_app.models.SaleDetailModel;
import com.example.food_delivery_app.utils.enums.InvoiceType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

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
          COALESCE(coupons.discount_value,0) AS discountValue,
          COALESCE(coupons.discount_type,'') AS discountType,
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
          INNER JOIN menus on item_code = menus.name and sale_detail.res_id = menus.res_id
          WHERE invoice_type = 'ORDER' AND users.user_id = :userId AND sale_header.status = true
              """, nativeQuery = true)
  List<CartItemResponseDto> GetCartByUserId(
      @Param("invoiceType") InvoiceType invoiceType,
      @Param("userId") int userId);

  SaleDetailModel findBySaleHeader_IdAndItemCode(int headerId, String itemCode);

  List<SaleDetailModel> findAllBySaleHeader_Id(int headerId);
}
