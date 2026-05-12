package com.group_one.food_delivery_app.Interfaces;

import java.util.List;

import org.springframework.stereotype.Service;

import com.group_one.food_delivery_app.dtos.repsonse.CartResponeDto;
@Service
public interface ICartService  {
    List<CartResponeDto> GetCartByUserId(int userId);
}
