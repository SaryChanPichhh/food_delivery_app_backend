package com.example.food_delivery_app.backend_user.controllers;

import java.util.Comparator;

import com.example.food_delivery_app.backend_user.interfaces.ICategoryService;
import com.example.food_delivery_app.backend_user.interfaces.ISaleService;
import com.example.food_delivery_app.backend_user.services.CouponService;
import com.example.food_delivery_app.dto.repsonse.RestaurantResponseDto;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import java.util.HashMap;
import java.util.Map;
import java.util.Comparator;

@RestController
@AllArgsConstructor
@RequestMapping("/")
public class HomeController {
    private final ICategoryService _categoryService;
    private final ISaleService _saleService;
    private final CouponService _discountService;

    @GetMapping("")
    public ResponseEntity<?> index(HttpSession session,
            @org.springframework.web.bind.annotation.RequestParam(required = false) String q) {
        Object userIdObj = session.getAttribute("userId");
        if (userIdObj == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
        int userId = (int) userIdObj;

        var cateInfo = _categoryService.GetData();
        var resInfo = _saleService.getRestaurantInfo(userId);

        // Filter by search query if provided
        if (q != null && !q.isEmpty()) {
            resInfo = resInfo.stream()
                    .filter(r -> r.getResName().toLowerCase().contains(q.toLowerCase()))
                    .toList();
        }

        var discountInfo = _discountService.getDiscountInfo(userId);
        var getMinDiscount = discountInfo.stream()
                .filter(d -> d.getDiscountValue() != null) // ✅ avoid null
                .min(Comparator.comparing(d -> d.getDiscountValue()))
                .orElse(null);
        var topRes = resInfo.stream()
                .sorted(Comparator.comparingInt(RestaurantResponseDto::getQty).reversed())
                .limit(4)
                .toList();

        Map<String, Object> response = new HashMap<>();
        if (getMinDiscount != null) {
            response.put("minDiscount", getMinDiscount.getDiscountValue());
            response.put("minAmount", getMinDiscount.getMinAmount());
        }

        System.out.println("user id : " + userId);
        response.put("cateInfo", cateInfo);
        response.put("activePage", "delivery");
        response.put("resInfo", resInfo);
        response.put("discountInfo", discountInfo);
        response.put("searchQuery", q);
        response.put("topRes", topRes);
        
        return ResponseEntity.ok(response);
    }

}
