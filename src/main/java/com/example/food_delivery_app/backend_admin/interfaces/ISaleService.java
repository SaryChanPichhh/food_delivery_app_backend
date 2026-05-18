package com.example.food_delivery_app.backend_admin.interfaces;

import com.example.food_delivery_app.dto.repsonse.RestaurantResponseDto;
import com.example.food_delivery_app.models.SaleHeaderModel;
import com.example.food_delivery_app.utils.enums.PaymentMethod;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
public interface ISaleService {
    List<RestaurantResponseDto> getRestaurantInfo(int userId);
    List<SaleHeaderModel> getAllSaleOrderByUserId(int userId);
    SaleHeaderModel getSaleOrderByUserIdAndId(int userId,int id);
    void addToCart(int userId, int menuId, int resId);
    SaleHeaderModel getActiveCart(int userId);
    void updateCartItemQuantity(int userId, int detailId, int changeAmount);
    void removeFromCart(int userId, int detailId);
    void checkoutCart(int userId, PaymentMethod paymentMethod, Long deliveryId);
    void checkoutCart(int userId, Long deliveryId);
}
