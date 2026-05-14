package com.example.food_delivery_app.controllers;

import com.example.food_delivery_app.Interfaces.ICartService;
import com.example.food_delivery_app.Interfaces.ISaleService;
import com.example.food_delivery_app.Interfaces.IUserService;
import com.example.food_delivery_app.repositories.DeliveryRepository;
import com.example.food_delivery_app.services.ExchangeRateService;
import com.example.food_delivery_app.utils.enums.PaymentMethod;
import com.example.food_delivery_app.services.CouponAssignmentService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping("/cart")
@AllArgsConstructor
public class CartController {
    private final CouponAssignmentService couponAssignmentService;
    private final ICartService cartService;
    private final IUserService userService;
    private final ISaleService saleService;
    private final ExchangeRateService exchangeRateService;
    private final DeliveryRepository deliveryRepository;

    @GetMapping("")
    public ResponseEntity<?> getMethodName(jakarta.servlet.http.HttpSession session) {
        Object userIdObj = session.getAttribute("userId");
        if (userIdObj == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
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

        Map<String, Object> response = new HashMap<>();
        response.put("coupons", coupon);
        response.put("carts", cart);
        response.put("subTotal", subTotal);
        response.put("totalDiscount", totalDiscount);
        response.put("grandTotal", grandTotal);
        response.put("userInfo", getUserInfo);
        
        // Add active delivery personnel
        var deliveries = deliveryRepository.findAll(); // Could filter by status
        response.put("deliveries", deliveries);
        
        var rate = exchangeRateService.getDefaultRate().map(r -> r.getRate()).orElse(4000.0);
        response.put("exchangeRate", rate);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/checkout")
    public ResponseEntity<?> checkout(jakarta.servlet.http.HttpSession session,
                         @RequestParam(value = "paymentMethod", required = false) String paymentMethod,
                         @RequestParam(value = "deliveryId", required = false) Long deliveryId) {
        var userIdInSession = session.getAttribute("userId");
        int userId = userIdInSession != null ? (int) userIdInSession : 1;
        PaymentMethod method;
        try {
            method = PaymentMethod.valueOf(paymentMethod != null ? paymentMethod : "CASH");
        } catch (IllegalArgumentException e) {
            method = PaymentMethod.CASH;
        }
        saleService.checkoutCart(userId, method, deliveryId);
        
        Map<String, String> response = new HashMap<>();
        response.put("success", "Checkout completed successfully");
        return ResponseEntity.ok(response);
    }   
    
}
