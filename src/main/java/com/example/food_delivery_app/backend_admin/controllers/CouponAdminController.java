package com.example.food_delivery_app.backend_admin.controllers;

import com.example.food_delivery_app.backend_admin.services.AdminCouponService;
import com.example.food_delivery_app.models.CouponModel;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/backend/coupons")
public class CouponAdminController {
    
    @Autowired
    private AdminCouponService couponService;
    
    // List all coupons with pagination and search
    @GetMapping("")
    public Map<String, Object> listCoupons(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "ALL") String status) {
        
        // Sync statuses with current time before listing
        couponService.updateExpiredCoupons();
        
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<CouponModel> couponPage;
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            List<CouponModel> searchResults = couponService.searchCoupons(keyword);
            int start = Math.min((int) pageable.getOffset(), searchResults.size());
            int end = Math.min((start + size), searchResults.size());
            List<CouponModel> pageContent = searchResults.subList(start, end);
            couponPage = new org.springframework.data.domain.PageImpl<>(pageContent, pageable, searchResults.size());
        } else if (!"ALL".equals(status)) {
            CouponModel.CouponStatus statusEnum = CouponModel.CouponStatus.valueOf(status);
            List<CouponModel> statusResults = couponService.getCouponsByStatus(statusEnum);
            int start = Math.min((int) pageable.getOffset(), statusResults.size());
            int end = Math.min((start + size), statusResults.size());
            List<CouponModel> pageContent = statusResults.subList(start, end);
            couponPage = new org.springframework.data.domain.PageImpl<>(pageContent, pageable, statusResults.size());
        } else {
            List<CouponModel> allCoupons = couponService.getAllCoupons();
            int start = Math.min((int) pageable.getOffset(), allCoupons.size());
            int end = Math.min((start + size), allCoupons.size());
            List<CouponModel> pageContent = allCoupons.subList(start, end);
            couponPage = new org.springframework.data.domain.PageImpl<>(pageContent, pageable, allCoupons.size());
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("couponPage", couponPage);
        response.put("keyword", keyword);
        response.put("status", status);
        response.put("statuses", CouponModel.CouponStatus.values());
        response.put("currentPage", page);
        response.put("totalPages", couponPage.getTotalPages());
        response.put("totalItems", couponPage.getTotalElements());
        response.put("coupons", couponPage.getContent());
        
        return response;
    }
    
    // Show create coupon data
    @GetMapping("/create")
    public Map<String, Object> showCreateForm() {
        Map<String, Object> response = new HashMap<>();
        response.put("coupon", new CouponModel());
        response.put("discountTypes", CouponModel.DiscountType.values());
        response.put("statuses", CouponModel.CouponStatus.values());
        response.put("pageTitle", "Create New Coupon");
        return response;
    }
    
    // Save new coupon
    @PostMapping("/store")
    public ResponseEntity<?> storeCoupon(@Valid @RequestBody CouponModel coupon,
                                     BindingResult result) {
        
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        
        try {
            couponService.createCoupon(coupon);
            Map<String, String> response = new HashMap<>();
            response.put("success", "Coupon created successfully!");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // Show edit coupon data
    @GetMapping("/edit/{id}")
    public ResponseEntity<?> showEditForm(@PathVariable Long id) {
        Optional<CouponModel> coupon = couponService.getCouponById(id);
        
        if (coupon.isPresent()) {
            Map<String, Object> response = new HashMap<>();
            response.put("coupon", coupon.get());
            response.put("discountTypes", CouponModel.DiscountType.values());
            response.put("statuses", CouponModel.CouponStatus.values());
            response.put("pageTitle", "Edit Coupon");
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Coupon not found");
            return ResponseEntity.status(404).body(response);
        }
    }
    
    // Update coupon
    @PostMapping("/update/{id}")
    public ResponseEntity<?> updateCoupon(@PathVariable Long id,
                                     @Valid @RequestBody CouponModel coupon,
                                     BindingResult result) {
        
        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }
        
        try {
            couponService.updateCoupon(id, coupon);
            Map<String, String> response = new HashMap<>();
            response.put("success", "Coupon updated successfully!");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // Delete coupon
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteCoupon(@PathVariable Long id) {
        try {
            couponService.deleteCoupon(id);
            Map<String, String> response = new HashMap<>();
            response.put("success", "Coupon deleted successfully!");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }
    
    // Toggle coupon status
    @PostMapping("/toggle/{id}")
    public ResponseEntity<?> toggleCouponStatus(@PathVariable Long id) {
        Optional<CouponModel> couponOpt = couponService.getCouponById(id);
        
        if (couponOpt.isPresent()) {
            CouponModel coupon = couponOpt.get();
            if (coupon.getStatus() == CouponModel.CouponStatus.ACTIVE) {
                coupon.setStatus(CouponModel.CouponStatus.INACTIVE);
            } else {
                coupon.setStatus(CouponModel.CouponStatus.ACTIVE);
            }
            couponService.updateCoupon(id, coupon);
            Map<String, String> response = new HashMap<>();
            response.put("success", "Coupon status updated successfully!");
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Coupon not found!");
            return ResponseEntity.status(404).body(response);
        }
    }
    
    // View coupon details
    @GetMapping("/view/{id}")
    public ResponseEntity<?> viewCoupon(@PathVariable Long id) {
        Optional<CouponModel> coupon = couponService.getCouponById(id);
        
        if (coupon.isPresent()) {
            return ResponseEntity.ok(coupon.get());
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Coupon not found");
            return ResponseEntity.status(404).body(response);
        }
    }
    
    // API endpoint for coupon validation
    @GetMapping("/api/validate/{code}")
    public CouponValidationResponse validateCouponApi(@PathVariable String code,
                                                   @RequestParam(required = false) Double orderAmount) {
        boolean isValid = couponService.validateCoupon(code, orderAmount);
        Optional<CouponModel> coupon = couponService.getCouponByCode(code);
        
        CouponValidationResponse response = new CouponValidationResponse();
        response.setValid(isValid);
        
        if (coupon.isPresent()) {
            CouponModel c = coupon.get();
            response.setDiscountType(c.getDiscountType());
            response.setDiscountValue(c.getDiscountValue());
            response.setMinOrderAmount(c.getMinOrderAmount());
            response.setDescription(c.getDescription());
        }
        
        return response;
    }
    
    // Response class for API
    public static class CouponValidationResponse {
        private boolean valid;
        private CouponModel.DiscountType discountType;
        private Double discountValue;
        private Double minOrderAmount;
        private String description;
        
        // Getters and Setters
        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }
        public CouponModel.DiscountType getDiscountType() { return discountType; }
        public void setDiscountType(CouponModel.DiscountType discountType) { this.discountType = discountType; }
        public Double getDiscountValue() { return discountValue; }
        public void setDiscountValue(Double discountValue) { this.discountValue = discountValue; }
        public Double getMinOrderAmount() { return minOrderAmount; }
        public void setMinOrderAmount(Double minOrderAmount) { this.minOrderAmount = minOrderAmount; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
}
