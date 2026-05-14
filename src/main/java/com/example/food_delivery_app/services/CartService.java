package com.example.food_delivery_app.services;

import java.util.List;

import com.example.food_delivery_app.Interfaces.ICartService;
import com.example.food_delivery_app.dtos.repsonse.CartResponeDto;
import com.example.food_delivery_app.repositories.CartRepository;
import com.example.food_delivery_app.utils.enums.InvoiceType;

import lombok.AllArgsConstructor;

import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CartService implements ICartService {
    private final CartRepository cartRepository;

    @Override
    public List<CartResponeDto> GetCartByUserId(int userId) {
        return cartRepository.GetCartByUserId(InvoiceType.ORDER, userId);
    }
}
