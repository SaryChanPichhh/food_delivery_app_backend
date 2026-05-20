package com.example.food_delivery_app.backend_admin.services;

import com.example.food_delivery_app.dto.repsonse.DiscountResponseDto;
import com.example.food_delivery_app.models.CouponModel;
import com.example.food_delivery_app.repositories.CouponRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AdminCouponService {

    @Autowired
    private CouponRepository couponRepository;

    // Get all coupons
    public List<CouponModel> getAllCoupons() {
        return couponRepository.findAll();
    }

    // Get coupon by ID
    public Optional<CouponModel> getCouponById(Long id) {
        return couponRepository.findById(id);
    }

    // Get coupon by code
    public Optional<CouponModel> getCouponByCode(String code) {
        return couponRepository.findByCode(code);
    }

    // Create new coupon
    public CouponModel createCoupon(CouponModel coupon) {
        // Check if code already exists
        if (couponRepository.existsByCode(coupon.getCode())) {
            throw new RuntimeException("Coupon code already exists: " + coupon.getCode());
        }

        // Validate dates
        if (coupon.getStartDate() != null && coupon.getEndDate() != null) {
            if (coupon.getEndDate().isBefore(coupon.getStartDate())) {
                throw new RuntimeException("End date must be after start date");
            }
        }

        coupon.setCreatedAt(LocalDateTime.now());
        coupon.setUpdatedAt(LocalDateTime.now());

        // Auto-set status if already expired
        if (coupon.isExpired()) {
            coupon.setStatus(CouponModel.CouponStatus.EXPIRED);
        }

        return couponRepository.save(coupon);
    }

    // Update coupon
    public CouponModel updateCoupon(Long id, CouponModel couponDetails) {
        Optional<CouponModel> existingCouponOpt = couponRepository.findById(id);

        if (existingCouponOpt.isPresent()) {
            CouponModel existingCoupon = existingCouponOpt.get();

            // Check if code already exists (excluding current coupon)
            if (couponRepository.existsByCodeAndCouponIdNot(couponDetails.getCode(), id)) {
                throw new RuntimeException("Coupon code already exists: " + couponDetails.getCode());
            }

            // Update fields
            // Validate dates
            if (couponDetails.getStartDate() != null && couponDetails.getEndDate() != null) {
                if (couponDetails.getEndDate().isBefore(couponDetails.getStartDate())) {
                    throw new RuntimeException("End date must be after start date");
                }
            }

            // Update fields
            existingCoupon.setCode(couponDetails.getCode());
            existingCoupon.setDescription(couponDetails.getDescription());
            existingCoupon.setDiscountType(couponDetails.getDiscountType());
            existingCoupon.setDiscountValue(couponDetails.getDiscountValue());
            existingCoupon.setMinOrderAmount(couponDetails.getMinOrderAmount());
            existingCoupon.setMaxUsage(couponDetails.getMaxUsage());
            existingCoupon.setStartDate(couponDetails.getStartDate());
            existingCoupon.setEndDate(couponDetails.getEndDate());
            existingCoupon.setStatus(couponDetails.getStatus());
            existingCoupon.setUpdatedAt(LocalDateTime.now());

            // Auto-update status based on new dates
            existingCoupon.updateStatusBasedOnDate();

            return couponRepository.save(existingCoupon);
        } else {
            throw new RuntimeException("Coupon not found with id: " + id);
        }
    }

    // Delete coupon
    public void deleteCoupon(Long id) {
        if (couponRepository.existsById(id)) {
            couponRepository.deleteById(id);
        } else {
            throw new RuntimeException("Coupon not found with id: " + id);
        }
    }

    // Get active coupons
    public List<CouponModel> getActiveCoupons() {
        return couponRepository.findActiveCoupons(LocalDateTime.now());
    }

    // Get coupons by status
    public List<CouponModel> getCouponsByStatus(CouponModel.CouponStatus status) {
        return couponRepository.findByStatus(status);
    }

    // Search coupons
    public List<CouponModel> searchCoupons(String keyword) {
        return couponRepository.searchCoupons(keyword);
    }

    // Validate coupon for usage
    public boolean validateCoupon(String code, Double orderAmount) {
        Optional<CouponModel> couponOpt = couponRepository.findByCode(code);

        if (!couponOpt.isPresent()) {
            return false;
        }

        CouponModel coupon = couponOpt.get();
        LocalDateTime now = LocalDateTime.now();

        // Check if coupon is active
        if (coupon.getStatus() != CouponModel.CouponStatus.ACTIVE) {
            return false;
        }

        // Check if coupon is within valid date range
        if (now.isBefore(coupon.getStartDate()) || now.isAfter(coupon.getEndDate())) {
            return false;
        }

        // Check minimum order amount
        if (orderAmount < coupon.getMinOrderAmount()) {
            return false;
        }

        // Check usage limit
        if (coupon.getMaxUsage() != null && coupon.getUsedCount() >= coupon.getMaxUsage()) {
            return false;
        }

        return true;
    }

    // Increment coupon usage count
    public void incrementCouponUsage(String code) {
        Optional<CouponModel> couponOpt = couponRepository.findByCode(code);

        if (couponOpt.isPresent()) {
            CouponModel coupon = couponOpt.get();
            coupon.setUsedCount(coupon.getUsedCount() + 1);
            couponRepository.save(coupon);
        }
    }

    // Update expired coupons
    // Update expired coupons
    public void updateExpiredCoupons() {
        LocalDateTime now = LocalDateTime.now();
        List<CouponModel> allCoupons = couponRepository.findAll();

        boolean changed = false;
        for (CouponModel coupon : allCoupons) {
            CouponModel.CouponStatus oldStatus = coupon.getStatus();
            coupon.updateStatusBasedOnDate();

            if (oldStatus != coupon.getStatus()) {
                couponRepository.save(coupon);
                changed = true;
            }
        }
    }

    // Get coupon statistics
    public CouponStats getCouponStats() {
        List<CouponModel> allCoupons = couponRepository.findAll();

        int totalCoupons = allCoupons.size();
        long activeCoupons = allCoupons.stream()
                .filter(c -> c.getStatus() == CouponModel.CouponStatus.ACTIVE)
                .count();
        long inactiveCoupons = allCoupons.stream()
                .filter(c -> c.getStatus() == CouponModel.CouponStatus.INACTIVE)
                .count();
        long expiredCoupons = allCoupons.stream()
                .filter(c -> c.getStatus() == CouponModel.CouponStatus.EXPIRED)
                .count();

        return new CouponStats(totalCoupons, (int) activeCoupons, (int) inactiveCoupons, (int) expiredCoupons);
    }

    // Inner class for statistics
    public static class CouponStats {
        private int total;
        private int active;
        private int inactive;
        private int expired;

        public CouponStats(int total, int active, int inactive, int expired) {
            this.total = total;
            this.active = active;
            this.inactive = inactive;
            this.expired = expired;
        }

        // Getters
        public int getTotal() {
            return total;
        }

        public int getActive() {
            return active;
        }

        public int getInactive() {
            return inactive;
        }

        public int getExpired() {
            return expired;
        }
    }

    public List<DiscountResponseDto> getDiscountInfo(int userId) {
        return couponRepository.getDiscountInfo(userId, LocalDateTime.now());
    }
}
