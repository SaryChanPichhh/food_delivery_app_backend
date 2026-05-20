package com.example.food_delivery_app.backend_user.controllers;

import com.example.food_delivery_app.backend_user.interfaces.ICartService;
import com.example.food_delivery_app.backend_user.interfaces.ISaleService;
import com.example.food_delivery_app.backend_user.interfaces.IUserService;
import com.example.food_delivery_app.backend_user.services.CouponAssignmentService;
import com.example.food_delivery_app.backend_user.services.ExchangeRateService;
import com.example.food_delivery_app.dto.repsonse.ApiResponse;
import com.example.food_delivery_app.repositories.DeliveryRepository;
import com.example.food_delivery_app.shared.constants.ApiRoutes;
import com.example.food_delivery_app.utils.enums.PaymentMethod;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;

@RestController
@RequestMapping(ApiRoutes.USER_CARTS)
@AllArgsConstructor
public class CartController {
    private final CouponAssignmentService couponAssignmentService;
    private final ICartService cartService;
    private final IUserService userService;
    private final ISaleService saleService;
    private final ExchangeRateService exchangeRateService;
    private final DeliveryRepository deliveryRepository;

    @GetMapping("")
    public ResponseEntity<?> getMethodName(Authentication auth) {
        Map<String, Object> user =
                (Map<String, Object>)
                        auth.getPrincipal();
        Integer userId =
                (Integer) user.get("userId");

        var cart = cartService.GetCartByUserId(userId);
        return ResponseEntity.ok(ApiResponse.builder().data(cart).message("fetch cart success").success(true).build());
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
