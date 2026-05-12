package com.group_one.food_delivery_app.controllers;
import java.util.List;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.group_one.food_delivery_app.Interfaces.ICartService;
import com.group_one.food_delivery_app.Interfaces.IUserService;
import com.group_one.food_delivery_app.dtos.repsonse.CartResponeDto;
import com.group_one.food_delivery_app.models.CouponAssignmentModel;
import com.group_one.food_delivery_app.services.CouponAssignmentService;
import com.group_one.food_delivery_app.services.CouponService;
import com.group_one.food_delivery_app.utils.helper.Convertor;

import lombok.AllArgsConstructor;

@Controller
@RequestMapping("/cart")
@AllArgsConstructor
public class CartController {
    private final CouponAssignmentService couponAssignmentService;
    private final ICartService cartService;
    private final IUserService userService;
    private final com.group_one.food_delivery_app.Interfaces.ISaleService saleService;
    private final com.group_one.food_delivery_app.services.ExchangeRateService exchangeRateService;
    private final com.group_one.food_delivery_app.repositories.DeliveryRepository deliveryRepository;
    @GetMapping("")
    public String getMethodName(jakarta.servlet.http.HttpSession session, Model model) {
        Object userIdObj = session.getAttribute("userId");
        if (userIdObj == null) {
            return "redirect:/auth/login";
        }
        int userId = (int) userIdObj;

        var coupon = couponAssignmentService.findByRestaurantResId(1); // Usually depends on cart resId
        var cart = cartService.GetCartByUserId(userId);
        var getUserInfo = userService.findById(userId);
        var subTotal = cart.stream()
        .mapToDouble(i -> i.getTotal() != null ? i.getTotal() : 0.0)
        .sum();

        var totalDiscount = cart.stream()
                .mapToDouble(i -> i.getDiscountValue() != null ? i.getDiscountValue() : 0.0)
                .sum();

        var grandTotal = subTotal - totalDiscount;

        model.addAttribute("coupons", coupon);
        model.addAttribute("carts", cart);
        model.addAttribute("subTotal", subTotal);
        model.addAttribute("totalDiscount", totalDiscount);
        model.addAttribute("grandTotal", grandTotal);
        model.addAttribute("userInfo", getUserInfo);
        
        // Add active delivery personnel
        var deliveries = deliveryRepository.findAll(); // Could filter by status
        model.addAttribute("deliveries", deliveries);
        
        var rate = exchangeRateService.getDefaultRate().map(r -> r.getRate()).orElse(4000.0);
        model.addAttribute("exchangeRate", rate);

        String cartJson = Convertor.FromFromListToJson(cart);
        String userInfoJson = Convertor.FromFromObjectToJson(getUserInfo);
        System.out.println(userInfoJson);
        return "cart/index";
    }

    @org.springframework.web.bind.annotation.PostMapping("/checkout")
    public String checkout(jakarta.servlet.http.HttpSession session,
                         @org.springframework.web.bind.annotation.RequestParam(value = "paymentMethod", required = false) String paymentMethod,
                         @org.springframework.web.bind.annotation.RequestParam(value = "deliveryId", required = false) Long deliveryId) {
        var userIdInSession = session.getAttribute("userId");
        int userId = userIdInSession != null ? (int) userIdInSession : 1;
        com.group_one.food_delivery_app.utils.enums.PaymentMethod method;
        try {
            method = com.group_one.food_delivery_app.utils.enums.PaymentMethod.valueOf(paymentMethod != null ? paymentMethod : "CASH");
        } catch (IllegalArgumentException e) {
            method = com.group_one.food_delivery_app.utils.enums.PaymentMethod.CASH;
        }
        saleService.checkoutCart(userId, method, deliveryId);
        return "redirect:/checkout";
    }   
    
}
