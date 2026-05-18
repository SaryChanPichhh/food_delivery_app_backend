package com.example.food_delivery_app.backend_user.controllers;

import com.example.food_delivery_app.backend_user.services.CouponService;
import com.example.food_delivery_app.dto.repsonse.ApiResponse;
import com.example.food_delivery_app.dto.repsonse.MenuResponseDto;
import com.example.food_delivery_app.dto.repsonse.DiscountResponeDto;
import com.example.food_delivery_app.utils.enums.DiscountValue;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/coupon")
public class CouponController {
    private final CouponService _discountService;

    @GetMapping("/greater-than-fifty")
    public ResponseEntity<ApiResponse<List<DiscountResponeDto>>>
            getResWhichDisGreaterThanFifty(Authentication auth){
        Map<String, Object> user =
                (Map<String, Object>)
                        auth.getPrincipal();
        Integer userId =
                (Integer) user.get("userId");

        var data =_discountService.getDiscountInfo(userId).stream()
                .filter(x->x.getDiscountValue()>DiscountValue.FIFTY.getValue()).toList();
        return ResponseEntity.ok(ApiResponse.<List<DiscountResponeDto>>builder()
                .success(true).message("fetch success").data(data).build());
    }
    @GetMapping("/greater-than-seventy")
    public ResponseEntity<ApiResponse<List<DiscountResponeDto>>>
    getResWhichDisGreaterThanSeventy(Authentication auth){
        Map<String, Object> user =
                (Map<String, Object>)
                        auth.getPrincipal();
        Integer userId =
                (Integer) user.get("userId");

        var data =_discountService.getDiscountInfo(userId)
                .stream().filter(x->x.getDiscountValue()>DiscountValue.SEVENTY.getValue()).toList();
        return ResponseEntity.ok(ApiResponse.<List<DiscountResponeDto>>builder()
                .success(true).message("fetch success").data(data).build());
    }

    @GetMapping("/greater-than-thirty")
    public ResponseEntity<ApiResponse<List<DiscountResponeDto>>>
    getResWhichDisLessThanThirty(Authentication auth){
        Map<String, Object> user =
                (Map<String, Object>)
                        auth.getPrincipal();
        Integer userId =
                (Integer) user.get("userId");

        var data =_discountService.getDiscountInfo(userId)
                .stream().filter(x->x.getDiscountValue()>DiscountValue.THIRTY.getValue())
                .toList();
        return ResponseEntity.ok(ApiResponse.<List<DiscountResponeDto>>builder().success(true).message("fetch success").data(data).build());
    }
    @GetMapping("/greater-than-zero")
    public ResponseEntity<ApiResponse<List<DiscountResponeDto>>>
    getResWhichDisLessThanZero(Authentication auth){
        Map<String, Object> user =
                (Map<String, Object>)
                        auth.getPrincipal();
        Integer userId =
                (Integer) user.get("userId");

        var data =_discountService.getDiscountInfo(userId)
                .stream().filter(x->x.getDiscountValue()> DiscountValue.ZERO.getValue()).toList();
        return ResponseEntity.ok(ApiResponse.<List<DiscountResponeDto>>builder().success(true).message("fetch success").data(data).build());
    }
    @GetMapping("/dis-on-menu")
    public ResponseEntity<ApiResponse<List<MenuResponseDto>>> getDiscountOnMenu(Authentication auth){
        Map<String, Object> user =
                (Map<String, Object>)
                        auth.getPrincipal();
        Integer userId =
                (Integer) user.get("userId");

        var data =_discountService.getDiscountOnMenuInfo(userId);
        return ResponseEntity.ok(ApiResponse.<List<MenuResponseDto>>builder().success(true).message("fetch success").data(data).build());
    }
}
