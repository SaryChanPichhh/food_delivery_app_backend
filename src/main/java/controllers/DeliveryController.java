package com.group_one.food_delivery_app.controllers;

import com.group_one.food_delivery_app.Interfaces.ICategoryService;
import com.group_one.food_delivery_app.Interfaces.IRestaurantService;
import com.group_one.food_delivery_app.Interfaces.ISaleService;
import com.group_one.food_delivery_app.Interfaces.IMenuService;
import com.group_one.food_delivery_app.Interfaces.IReviewService;
import com.group_one.food_delivery_app.dtos.repsonse.DiscountResponeDto;
import com.group_one.food_delivery_app.dtos.repsonse.RestaurantResponseDto;
import com.group_one.food_delivery_app.services.CouponService;
import com.group_one.food_delivery_app.utils.helper.Convertor;

import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;

import java.util.Comparator;
import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/delivery")
@AllArgsConstructor
public class DeliveryController {
    private final IRestaurantService restaurantService;
    private final ISaleService _saleService;
    private final ICategoryService _categoryService;
    private final CouponService _discountService;
    private final IMenuService _menuService;
    private final IReviewService _reviewService;
    private final com.group_one.food_delivery_app.services.ExchangeRateService _exchangeRateService;
    
    @GetMapping("")
    public String index(HttpSession session, Model model){
        Object userIdObj = session.getAttribute("userId");
        if (userIdObj == null) {
            return "redirect:/auth/login";
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
         if (getMinDiscount != null) {
            var getMinAmount = getMinDiscount.getMinAmount();
            model.addAttribute("minDiscount", getMinDiscount.getDiscountValue());
            model.addAttribute("minAmount", getMinAmount);
        }
    
        System.out.println("DEBUG: Rendering index for user: " + userId);
        System.out.println("DEBUG: Top restaurants count: " + topRes.size());
        model.addAttribute("topRes", topRes);
        model.addAttribute("cateInfo", cateInfo);
        model.addAttribute("activePage", "delivery");
        model.addAttribute("resInfo", resInfo);
        model.addAttribute("discountInfo", discountInfo);
        
        var rate = _exchangeRateService.getDefaultRate().map(r -> r.getRate()).orElse(4000.0);
        model.addAttribute("exchangeRate", rate);
        
        return "delivery/index";
    }
    @GetMapping("/restuarant-detail")
    public String restuarantDetail(@RequestParam("res_id") Integer resId, HttpSession session, Model model) {
        Object userIdObj = session.getAttribute("userId");
        if (userIdObj == null) {
            return "redirect:/auth/login";
        }
        int userId = (int) userIdObj;
        var resInfo = _saleService.getRestaurantInfo(userId);
        System.out.println("DEBUG: Fetching details for resId: " + resId + ", userId: " + userId);
        System.out.println("DEBUG: Available restaurants count from service: " + resInfo.size());
        
        var resInfoByResId = resInfo.stream()
            .filter(r -> r.getResId() != null && String.valueOf(r.getResId()).trim().equals(String.valueOf(resId).trim()))
            .findFirst()
            .orElse(null);
            
        if (resInfoByResId == null) {
            System.out.println("ERROR: Restaurant not found for ID: " + resId);
            model.addAttribute("resInfoByResId", null); 
            return "redirect:/delivery"; 
        }
        
        System.out.println("Found Restaurant: " + resInfoByResId.getResName());
        
        model.addAttribute("resInfoByResId", resInfoByResId);

        // Fetch menus for the restaurant
        var menus = _menuService.getMenuItemsByRestaurant(resId);
        model.addAttribute("menus", menus);

        // Fetch Active Cart
        var activeCart = _saleService.getActiveCart(userId);
        
        if (activeCart != null && activeCart.getSaleDetails() != null) {
            model.addAttribute("cartItems", activeCart.getSaleDetails());
            model.addAttribute("cartSubTotal", activeCart.getTotal());
            model.addAttribute("exchangeRate", activeCart.getExchangeRate());
        } else {
            model.addAttribute("cartItems", null);
            model.addAttribute("cartSubTotal", 0.0);
            var rate = _exchangeRateService.getDefaultRate().map(r -> r.getRate()).orElse(4000.0);
            model.addAttribute("exchangeRate", rate);
        }

        // Fetch Reviews
        var reviews = _reviewService.getReviewsByRestaurant(resId);
        model.addAttribute("reviews", reviews);

        return "delivery/restuarant-detail";
    }

    @org.springframework.web.bind.annotation.PostMapping("/add-to-cart")
    public String addToCart(@RequestParam("resId") Integer resId, @RequestParam("menuId") Integer menuId, HttpSession session) {
        Object userIdObj = session.getAttribute("userId");
        if (userIdObj == null) {
            return "redirect:/auth/login";
        }
        int userId = (int) userIdObj;
        System.out.println("user id : " + userId);
        _saleService.addToCart(userId, menuId, resId);

        return "redirect:/delivery/restuarant-detail?res_id=" + resId;
    }

    @org.springframework.web.bind.annotation.PostMapping("/update-cart-item")
    public String updateCartItem(@RequestParam("resId") Integer resId, 
                                 @RequestParam("detailId") Integer detailId, 
                                 @RequestParam("action") String action, 
                                 HttpSession session) {
        Object userIdObj = session.getAttribute("userId");
        if (userIdObj == null) {
            return "redirect:/auth/login";
        }
        int userId = (int) userIdObj;

        if ("minus".equals(action)) {
            _saleService.updateCartItemQuantity(userId, detailId, -1);
        } else if ("delete".equals(action)) {
            _saleService.removeFromCart(userId, detailId);
        }

        return "redirect:/delivery/restuarant-detail?res_id=" + resId;
    }
    
    @GetMapping("/cuisine-detail")
    public String cuisineDetail(@RequestParam("cate_id") int cateId, HttpSession session, Model model) {
        Object userIdObj = session.getAttribute("userId");
        int userId = userIdObj != null ? (int) userIdObj : 1;
        var resInfoByCateId = _categoryService.GetCategoryDetail(cateId, userId);
        System.out.println(Convertor.FromFromListToJson(resInfoByCateId));
        model.addAttribute("resInfoByCateId", resInfoByCateId);

        var rate = _exchangeRateService.getDefaultRate().map(r -> r.getRate()).orElse(4000.0);
        model.addAttribute("exchangeRate", rate);

        return "delivery/cuisine-detail";
    }
}
