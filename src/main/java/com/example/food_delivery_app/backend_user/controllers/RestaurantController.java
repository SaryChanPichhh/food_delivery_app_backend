package com.example.food_delivery_app.backend_user.controllers;

import com.example.food_delivery_app.backend_user.interfaces.IRestaurantService;
import com.example.food_delivery_app.dto.repsonse.ApiResponse;
import com.example.food_delivery_app.dto.repsonse.RestaurantResponseDto;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Comparator;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/restaurant")
@AllArgsConstructor
public class RestaurantController {
    private final IRestaurantService _restaurantService;
    @GetMapping("/popular-restaurant")
    public ResponseEntity<?> getPopularRestaurant(Authentication auth){
        Map<String, Object> user =
                (Map<String, Object>)
                        auth.getPrincipal();
        Integer userId =
                (Integer) user.get("userId");

        String userName =
                (String) user.get("userName");
        var getPopularResInfo = _restaurantService.GetPopularRestaurant(userId);
        var data = getPopularResInfo.stream()
                .sorted(Comparator.comparingInt(RestaurantResponseDto::getQty).reversed())
                .limit(4)
                .toList();
        return ResponseEntity.ok(ApiResponse.builder().data(data).success(true).message("fetch success").build());
    }

    @GetMapping("")
    public ResponseEntity<?> getAllRestaurant(Authentication auth){
        Map<String, Object> user =
                (Map<String, Object>)
                        auth.getPrincipal();
        Integer userId =
                (Integer) user.get("userId");

        String userName =
                (String) user.get("userName");
        var getPopularResInfo = _restaurantService.GetData();
        return ResponseEntity.ok(ApiResponse.builder().data(getPopularResInfo).success(true).message("fetch success").build());
    }
}
