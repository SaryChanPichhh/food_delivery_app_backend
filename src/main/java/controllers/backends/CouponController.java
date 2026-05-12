package com.group_one.food_delivery_app.controllers.backends;

import com.group_one.food_delivery_app.models.CouponModel;
import com.group_one.food_delivery_app.services.CouponService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/backend/coupons")
public class CouponController {
    
    @Autowired
    private CouponService couponService;
    
    // List all coupons with pagination and search
    @GetMapping("")
    public String listCoupons(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "ALL") String status,
            Model model) {
        
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
        
        model.addAttribute("couponPage", couponPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("status", status);
        model.addAttribute("statuses", CouponModel.CouponStatus.values());
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", couponPage.getTotalPages());
        model.addAttribute("totalItems", couponPage.getTotalElements());
        
        // Add statistics
        CouponService.CouponStats stats = couponService.getCouponStats();
        model.addAttribute("coupons", couponPage.getContent());
        model.addAttribute("newCoupon", new CouponModel());
        
        return "backend/coupons/index";
    }
    
    // Show create coupon form
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("coupon", new CouponModel());
        model.addAttribute("discountTypes", CouponModel.DiscountType.values());
        model.addAttribute("statuses", CouponModel.CouponStatus.values());
        model.addAttribute("pageTitle", "Create New Coupon");
        model.addAttribute("formAction", "/backend/coupons/store");
        return "backend/coupon/create";
    }
    
    // Save new coupon
    @PostMapping("/store")
    public String storeCoupon(@Valid @ModelAttribute("coupon") CouponModel coupon,
                             BindingResult result,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            model.addAttribute("discountTypes", CouponModel.DiscountType.values());
            model.addAttribute("statuses", CouponModel.CouponStatus.values());
            model.addAttribute("pageTitle", "Create New Coupon");
            model.addAttribute("formAction", "/backend/coupons/store");
            return "backend/coupon/create";
        }
        
        try {
            couponService.createCoupon(coupon);
            redirectAttributes.addFlashAttribute("success", "Coupon created successfully!");
            return "redirect:/backend/coupons";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("discountTypes", CouponModel.DiscountType.values());
            model.addAttribute("statuses", CouponModel.CouponStatus.values());
            model.addAttribute("pageTitle", "Create New Coupon");
            model.addAttribute("formAction", "/backend/coupons/store");
            return "backend/coupon/create";
        }
    }
    
    // Show edit coupon form
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Optional<CouponModel> coupon = couponService.getCouponById(id);
        
        if (coupon.isPresent()) {
            model.addAttribute("coupon", coupon.get());
            model.addAttribute("discountTypes", CouponModel.DiscountType.values());
            model.addAttribute("statuses", CouponModel.CouponStatus.values());
            model.addAttribute("pageTitle", "Edit Coupon");
            model.addAttribute("formAction", "/backend/coupons/update/" + id);
            return "backend/coupon/create";
        } else {
            return "redirect:/backend/coupons";
        }
    }
    
    // Update coupon
    @PostMapping("/update/{id}")
    public String updateCoupon(@PathVariable Long id,
                             @Valid @ModelAttribute("coupon") CouponModel coupon,
                             BindingResult result,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        
        if (result.hasErrors()) {
            model.addAttribute("discountTypes", CouponModel.DiscountType.values());
            model.addAttribute("statuses", CouponModel.CouponStatus.values());
            model.addAttribute("pageTitle", "Edit Coupon");
            model.addAttribute("formAction", "/backend/coupons/update/" + id);
            return "backend/coupon/create";
        }
        
        try {
            couponService.updateCoupon(id, coupon);
            redirectAttributes.addFlashAttribute("success", "Coupon updated successfully!");
            return "redirect:/backend/coupons";
        } catch (RuntimeException e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("discountTypes", CouponModel.DiscountType.values());
            model.addAttribute("statuses", CouponModel.CouponStatus.values());
            model.addAttribute("pageTitle", "Edit Coupon");
            model.addAttribute("formAction", "/backend/coupons/update/" + id);
            return "backend/coupon/create";
        }
    }
    
    // Delete coupon
    @GetMapping("/delete/{id}")
    public String deleteCoupon(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            couponService.deleteCoupon(id);
            redirectAttributes.addFlashAttribute("success", "Coupon deleted successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/backend/coupons";
    }
    
    // Toggle coupon status
    @GetMapping("/toggle/{id}")
    public String toggleCouponStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        Optional<CouponModel> couponOpt = couponService.getCouponById(id);
        
        if (couponOpt.isPresent()) {
            CouponModel coupon = couponOpt.get();
            if (coupon.getStatus() == CouponModel.CouponStatus.ACTIVE) {
                coupon.setStatus(CouponModel.CouponStatus.INACTIVE);
            } else {
                coupon.setStatus(CouponModel.CouponStatus.ACTIVE);
            }
            couponService.updateCoupon(id, coupon);
            redirectAttributes.addFlashAttribute("success", "Coupon status updated successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Coupon not found!");
        }
        
        return "redirect:/backend/coupons";
    }
    
    // View coupon details
    @GetMapping("/view/{id}")
    public String viewCoupon(@PathVariable Long id, Model model) {
        Optional<CouponModel> coupon = couponService.getCouponById(id);
        
        if (coupon.isPresent()) {
            model.addAttribute("coupon", coupon.get());
            return "backend/coupon/view";
        } else {
            return "redirect:/backend/coupons";
        }
    }
    
    // API endpoint for coupon validation
    @GetMapping("/api/validate/{code}")
    @ResponseBody
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
