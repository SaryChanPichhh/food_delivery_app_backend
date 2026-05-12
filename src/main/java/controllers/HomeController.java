package com.group_one.food_delivery_app.controllers;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.group_one.food_delivery_app.Interfaces.ICategoryService;
import com.group_one.food_delivery_app.Interfaces.ISaleService;
import com.group_one.food_delivery_app.dtos.repsonse.RestaurantResponseDto;
import com.group_one.food_delivery_app.services.CouponService;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class HomeController {
    private final ICategoryService _categoryService;
    private final ISaleService _saleService;
    private final CouponService _discountService;
    @GetMapping("")
    public String index(HttpSession session, Model model, 
                        @org.springframework.web.bind.annotation.RequestParam(required = false) String q) {
        Object userIdObj = session.getAttribute("userId");
        if (userIdObj == null) {
            return "redirect:/auth/login";
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
    .filter(d -> d.getDiscountValue() != null)   // ✅ avoid null
    .min(Comparator.comparing(d -> d.getDiscountValue()))
    .orElse(null);
        var topRes = resInfo.stream()
        .sorted(Comparator.comparingInt(RestaurantResponseDto::getQty).reversed())
        .limit(4)
        .toList();
         if (getMinDiscount != null) {
            var getMinAmount = getMinDiscount.getMinAmount();
            model.addAttribute("minDiscount", getMinDiscount.getDiscountValue());
            model.addAttribute("minAmount", getMinAmount);
        }
    
        System.out.println("user id : " + userId);
        model.addAttribute("cateInfo", cateInfo);
        model.addAttribute("activePage", "delivery");
        model.addAttribute("resInfo", resInfo);
        model.addAttribute("discountInfo", discountInfo);
        model.addAttribute("searchQuery", q);
        return "delivery/index";
    }
    
}
