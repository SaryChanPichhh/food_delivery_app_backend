package com.example.food_delivery_app.backend_user.services;

import com.example.food_delivery_app.backend_user.interfaces.ICartService;
import com.example.food_delivery_app.dto.repsonse.CartItemDto;
import com.example.food_delivery_app.dto.repsonse.CartItemResponseDto;
import com.example.food_delivery_app.dto.repsonse.CartRestaurantResponseDto;
import com.example.food_delivery_app.repositories.CartRepository;
import com.example.food_delivery_app.utils.enums.InvoiceType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
@Slf4j
@Service
@AllArgsConstructor
public class CartService implements ICartService {
    private final CartRepository cartRepository;

    @Override
    public List<CartRestaurantResponseDto> GetCartByUserId(int userId) {
        log.info("get cart by user id = {}",userId);
        var data = cartRepository.GetCartByUserId(InvoiceType.ORDER,userId)
                .stream().collect(Collectors.groupingBy(CartItemResponseDto::getResId))
                .values().stream().map(y->{
                    CartItemResponseDto first = y.get(0);
                    var resInfo = new CartRestaurantResponseDto();
                    resInfo.setResId(first.getResId());
                    resInfo.setResName(first.getResName());
                    resInfo.setResImage(first.getResImage());
                    resInfo.setResDesc(first.getResDesc());
                    resInfo.setIsOpen(first.getIsOpen());
                    resInfo.setAddress(first.getAddress());
                    resInfo.setAvgEstimateTime(first.getAvgEstimateTime());

                    List<CartItemDto> items = y.stream()
                            .map(x -> {
                                var item = new CartItemDto();
                                item.setHeaderId(x.getHeaderId());
                                item.setDetailId(x.getDetailId());
                                item.setItemCode(x.getItemCode());
                                item.setMenuDescription(x.getMenuDescription());
                                item.setMenuImage(x.getMenuImage());
                                item.setQty(x.getQty());
                                item.setSalePrice(x.getSalePrice());
                                item.setTotal(x.getTotal());
                                item.setDiscountValue(x.getDiscountValue());
                                var totalAfterDiscount = x.getTotal() - (x.getDiscountValue() * x.getTotal());
                                item.setTotalAfterDiscount(totalAfterDiscount);
                                var savingValue = x.getTotal() - totalAfterDiscount;
                                item.setSavingValue(savingValue);
                                return item;
                            })
                            .toList();
                    var subTotal = items.stream().mapToDouble(CartItemDto::getTotal).sum();
                    var totalDiscount = items.stream().mapToDouble(CartItemDto::getDiscountValue).sum();
                    var totalAfterDiscount = items.stream().mapToDouble(CartItemDto::getTotalAfterDiscount).sum();
                    resInfo.setSubTotal(subTotal);
                    resInfo.setTotalDiscount(totalDiscount);
                    resInfo.setTotalAfterDiscount(totalAfterDiscount);
                    resInfo.setItems(items);
                    return resInfo;
                }).toList();
        return data;
    }
}
