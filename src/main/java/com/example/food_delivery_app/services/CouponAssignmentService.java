package com.example.food_delivery_app.services;

import com.example.food_delivery_app.models.CouponAssignmentModel;
import com.example.food_delivery_app.repositories.CouponAssignmentRepository;
import com.example.food_delivery_app.repositories.MenuRepository;
import com.example.food_delivery_app.repositories.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CouponAssignmentService {
    @Autowired
    private CouponAssignmentRepository couponAssignmentRepository;

    @Autowired
    private RestaurantRepository restaurantRepository;

    @Autowired
    private MenuRepository menuRepository;


    // Get coupon by restaurant resId
    public List<CouponAssignmentModel> findByRestaurantResId(Integer resId) {
        return couponAssignmentRepository.findByRestaurantResId(resId);
    }
    
    // Get all assignments
    public Page<CouponAssignmentModel> getAllAssignments(int page, int size, String keyword) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        if (keyword != null && !keyword.trim().isEmpty()) {
            List<CouponAssignmentModel> searchResults = couponAssignmentRepository.searchAssignments(keyword);
            int start = Math.min((int) pageable.getOffset(), searchResults.size());
            int end = Math.min(start + size, searchResults.size());
            List<CouponAssignmentModel> pageContent = searchResults.subList(start, end);
            return new org.springframework.data.domain.PageImpl<>(pageContent, pageable, searchResults.size());
        }

        return couponAssignmentRepository.findAll(pageable);
    }

    // Get assignment by ID
    public Optional<CouponAssignmentModel> getAssignmentById(Long id) {
        return couponAssignmentRepository.findById(id);
    }

    // Create new assignment
    public CouponAssignmentModel createAssignment(CouponAssignmentModel assignment) {
        // Validate assignment
        validateAssignment(assignment);

        assignment.setCreatedAt(LocalDateTime.now());
        assignment.setUpdatedAt(LocalDateTime.now());

        return couponAssignmentRepository.save(assignment);
    }

    // Update assignment
    public CouponAssignmentModel updateAssignment(Long id, CouponAssignmentModel assignmentDetails) {
        Optional<CouponAssignmentModel> existingAssignmentOpt = couponAssignmentRepository.findById(id);

        if (existingAssignmentOpt.isPresent()) {
            CouponAssignmentModel existingAssignment = existingAssignmentOpt.get();

            // Validate the assignment
            validateAssignment(assignmentDetails);

            // Update fields
            existingAssignment.setCoupon(assignmentDetails.getCoupon());
            existingAssignment.setRestaurant(assignmentDetails.getRestaurant());
            existingAssignment.setMenuItem(assignmentDetails.getMenuItem());
            existingAssignment.setAssignmentType(assignmentDetails.getAssignmentType());
            existingAssignment.setNotes(assignmentDetails.getNotes());
            existingAssignment.setStatus(assignmentDetails.getStatus());
            existingAssignment.setUpdatedAt(LocalDateTime.now());

            return couponAssignmentRepository.save(existingAssignment);
        } else {
            throw new RuntimeException("Assignment not found with id: " + id);
        }
    }

    // Delete assignment
    public void deleteAssignment(Long id) {
        if (couponAssignmentRepository.existsById(id)) {
            couponAssignmentRepository.deleteById(id);
        } else {
            throw new RuntimeException("Assignment not found with id: " + id);
        }
    }

    // Get assignments by coupon
    public List<CouponAssignmentModel> getAssignmentsByCoupon(Long couponId) {
        return couponAssignmentRepository.findByCouponCouponId(couponId);
    }

    // Get assignments by restaurant
    public List<CouponAssignmentModel> getAssignmentsByRestaurant(Integer restaurantId) {
        return couponAssignmentRepository.findByRestaurantResId(restaurantId);
    }

    // Get assignments by menu item
    public List<CouponAssignmentModel> getAssignmentsByMenuItem(Integer menuItemId) {
        return couponAssignmentRepository.findByMenuItemId(menuItemId);
    }

    // Get active assignments
    public List<CouponAssignmentModel> getActiveAssignments() {
        return couponAssignmentRepository.findActiveAssignments();
    }

    // Toggle assignment status
    public CouponAssignmentModel toggleAssignmentStatus(Long id) {
        Optional<CouponAssignmentModel> assignmentOpt = couponAssignmentRepository.findById(id);

        if (assignmentOpt.isPresent()) {
            CouponAssignmentModel assignment = assignmentOpt.get();

            if (assignment.getStatus() == CouponAssignmentModel.AssignmentStatus.ACTIVE) {
                assignment.setStatus(CouponAssignmentModel.AssignmentStatus.INACTIVE);
            } else {
                assignment.setStatus(CouponAssignmentModel.AssignmentStatus.ACTIVE);
            }

            assignment.setUpdatedAt(LocalDateTime.now());
            return couponAssignmentRepository.save(assignment);
        } else {
            throw new RuntimeException("Assignment not found with id: " + id);
        }
    }

    // Get assignment statistics
    public AssignmentStatistics getAssignmentStatistics() {
        List<CouponAssignmentModel> allAssignments = couponAssignmentRepository.findAll();

        long total = allAssignments.size();
        long active = allAssignments.stream()
                .filter(a -> a.getStatus() == CouponAssignmentModel.AssignmentStatus.ACTIVE)
                .count();
        long inactive = allAssignments.stream()
                .filter(a -> a.getStatus() == CouponAssignmentModel.AssignmentStatus.INACTIVE)
                .count();
        long expired = allAssignments.stream()
                .filter(a -> a.getStatus() == CouponAssignmentModel.AssignmentStatus.EXPIRED)
                .count();

        long restaurantAssignments = allAssignments.stream()
                .filter(a -> a.getAssignmentType() == CouponAssignmentModel.AssignmentType.RESTAURANT)
                .count();
        long menuItemAssignments = allAssignments.stream()
                .filter(a -> a.getAssignmentType() == CouponAssignmentModel.AssignmentType.MENU_ITEM)
                .count();

        return new AssignmentStatistics((int) total, (int) active, (int) inactive, (int) expired,
                (int) restaurantAssignments, (int) menuItemAssignments);
    }

    // Validation methods
    private void validateAssignment(CouponAssignmentModel assignment) {
        validateAssignmentType(assignment);

        // Ensure only the relevant target is set based on type
        if (assignment.getAssignmentType() == CouponAssignmentModel.AssignmentType.RESTAURANT) {
            assignment.setMenuItem(null);
        } else if (assignment.getAssignmentType() == CouponAssignmentModel.AssignmentType.MENU_ITEM) {
            assignment.setRestaurant(null);
        }

        // Check if assignment has at least one target
        if (assignment.getRestaurant() == null && assignment.getMenuItem() == null) {
            throw new RuntimeException("Assignment must target either a restaurant OR a menu item");
        }
    }

    private void validateAssignmentType(CouponAssignmentModel assignment) {
        if (assignment.getAssignmentType() == CouponAssignmentModel.AssignmentType.RESTAURANT && assignment.getRestaurant() == null) {
            throw new RuntimeException("Restaurant assignment type requires a restaurant target");
        }

        if (assignment.getAssignmentType() == CouponAssignmentModel.AssignmentType.MENU_ITEM && assignment.getMenuItem() == null) {
            throw new RuntimeException("Menu item assignment type requires a menu item target");
        }
    }

    // Inner class for statistics
    public static class AssignmentStatistics {
        private int total;
        private int active;
        private int inactive;
        private int expired;
        private int restaurantAssignments;
        private int menuItemAssignments;

        public AssignmentStatistics(int total, int active, int inactive, int expired,
                                    int restaurantAssignments, int menuItemAssignments) {
            this.total = total;
            this.active = active;
            this.inactive = inactive;
            this.expired = expired;
            this.restaurantAssignments = restaurantAssignments;
            this.menuItemAssignments = menuItemAssignments;
        }

        // Getters
        public int getTotal() { return total; }
        public int getActive() { return active; }
        public int getInactive() { return inactive; }
        public int getExpired() { return expired; }
        public int getRestaurantAssignments() { return restaurantAssignments; }
        public int getMenuItemAssignments() { return menuItemAssignments; }
    }
}
