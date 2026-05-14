package com.example.food_delivery_app.Interfaces;

import com.example.food_delivery_app.dtos.repsonse.RestaurantResponseDto;
import com.example.food_delivery_app.utils.enums.PaymentMethod;
import com.example.food_delivery_app.models.SaleHeaderModel;

import java.util.List;

import org.springframework.stereotype.Service;
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
