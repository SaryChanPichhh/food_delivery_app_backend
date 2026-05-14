package com.example.food_delivery_app.controllers;

import com.example.food_delivery_app.Interfaces.*;
import com.example.food_delivery_app.services.ExchangeRateService;
import com.example.food_delivery_app.dtos.repsonse.DiscountResponeDto;
import com.example.food_delivery_app.dtos.repsonse.RestaurantResponseDto;
import com.example.food_delivery_app.services.CouponService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import java.util.Comparator;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/delivery")
@AllArgsConstructor
public class DeliveryController {
    private final IRestaurantService restaurantService;
    private final ISaleService _saleService;
    private final ICategoryService _categoryService;
    private final CouponService _discountService;
    private final IMenuService _menuService;
    private final IReviewService _reviewService;
    private final ExchangeRateService _exchangeRateService;
    
    @GetMapping("")
    public ResponseEntity<?> index(HttpSession session){
        Object userIdObj = session.getAttribute("userId");
        if (userIdObj == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
        int userId = (int) userIdObj;
        var cateInfo = _categoryService.GetData();
        var resInfo = _saleService.getRestaurantInfo(userId);

        var discountInfo = _discountService.getDiscountInfo(userId);
        var getMinDiscount = discountInfo.stream()
    .filter(d -> d.getDiscountValue() != null)   // ✅ avoid null
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
    
        System.out.println("DEBUG: Rendering index for user: " + userId);
        response.put("topRes", topRes);
        response.put("cateInfo", cateInfo);
        response.put("activePage", "delivery");
        response.put("resInfo", resInfo);
        response.put("discountInfo", discountInfo);
        
        var rate = _exchangeRateService.getDefaultRate().map(r -> r.getRate()).orElse(4000.0);
        response.put("exchangeRate", rate);
        
        return ResponseEntity.ok(response);
    }

    @GetMapping("/restuarant-detail")
    public ResponseEntity<?> restuarantDetail(@RequestParam("res_id") Integer resId, HttpSession session) {
        Object userIdObj = session.getAttribute("userId");
        if (userIdObj == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
        int userId = (int) userIdObj;
        var resInfo = _saleService.getRestaurantInfo(userId);
        
        var resInfoByResId = resInfo.stream()
            .filter(r -> r.getResId() != null && String.valueOf(r.getResId()).trim().equals(String.valueOf(resId).trim()))
            .findFirst()
            .orElse(null);
            
        Map<String, Object> response = new HashMap<>();
        if (resInfoByResId == null) {
            response.put("error", "Restaurant not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        
        response.put("resInfoByResId", resInfoByResId);

        // Fetch menus for the restaurant
        var menus = _menuService.getMenuItemsByRestaurant(resId);
        response.put("menus", menus);

        // Fetch Active Cart
        var activeCart = _saleService.getActiveCart(userId);
        
        if (activeCart != null && activeCart.getSaleDetails() != null) {
            response.put("cartItems", activeCart.getSaleDetails());
            response.put("cartSubTotal", activeCart.getTotal());
            response.put("exchangeRate", activeCart.getExchangeRate());
        } else {
            response.put("cartItems", null);
            response.put("cartSubTotal", 0.0);
            var rate = _exchangeRateService.getDefaultRate().map(r -> r.getRate()).orElse(4000.0);
            response.put("exchangeRate", rate);
        }

        // Fetch Reviews
        var reviews = _reviewService.getReviewsByRestaurant(resId);
        response.put("reviews", reviews);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/add-to-cart")
    public ResponseEntity<?> addToCart(@RequestParam("resId") Integer resId, @RequestParam("menuId") Integer menuId, HttpSession session) {
        Object userIdObj = session.getAttribute("userId");
        if (userIdObj == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
        int userId = (int) userIdObj;
        _saleService.addToCart(userId, menuId, resId);

        Map<String, String> response = new HashMap<>();
        response.put("success", "Item added to cart");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/update-cart-item")
    public ResponseEntity<?> updateCartItem(@RequestParam("resId") Integer resId, 
                                 @RequestParam("detailId") Integer detailId, 
                                 @RequestParam("action") String action, 
                                 HttpSession session) {
        Object userIdObj = session.getAttribute("userId");
        if (userIdObj == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
        int userId = (int) userIdObj;

        if ("minus".equals(action)) {
            _saleService.updateCartItemQuantity(userId, detailId, -1);
        } else if ("delete".equals(action)) {
            _saleService.removeFromCart(userId, detailId);
        }

        Map<String, String> response = new HashMap<>();
        response.put("success", "Cart updated");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/cuisine-detail")
    public ResponseEntity<?> cuisineDetail(@RequestParam("cate_id") int cateId, HttpSession session) {
        Object userIdObj = session.getAttribute("userId");
        int userId = userIdObj != null ? (int) userIdObj : 1;
        var resInfoByCateId = _categoryService.GetCategoryDetail(cateId, userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("resInfoByCateId", resInfoByCateId);

        var rate = _exchangeRateService.getDefaultRate().map(r -> r.getRate()).orElse(4000.0);
        response.put("exchangeRate", rate);

        return ResponseEntity.ok(response);
    }
}
