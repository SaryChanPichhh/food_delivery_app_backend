package com.example.food_delivery_app.controllers.backends;

import com.example.food_delivery_app.models.CouponAssignmentModel;
import com.example.food_delivery_app.models.MenuModel;
import com.example.food_delivery_app.services.CouponAssignmentService;
import com.example.food_delivery_app.services.CouponService;
import com.example.food_delivery_app.services.MenuService;
import com.example.food_delivery_app.services.RestaurantService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/backend/coupon-assignments")
public class CouponAssignmentController {
    @Autowired
    private CouponAssignmentService couponAssignmentService;

    @Autowired
    private CouponService couponService;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private MenuService menuService;

    // List all assignments with pagination and search
    @GetMapping("")
    public Map<String, Object> listAssignments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String keyword) {

        Page<CouponAssignmentModel> assignmentPage = couponAssignmentService.getAllAssignments(page, size, keyword);

        Map<String, Object> response = new HashMap<>();
        response.put("assignmentPage", assignmentPage);
        response.put("keyword", keyword);
        response.put("currentPage", page);
        response.put("totalPages", assignmentPage.getTotalPages());
        response.put("totalItems", assignmentPage.getTotalElements());
        response.put("stats", couponAssignmentService.getAssignmentStatistics());

        return response;
    }

    // Show create assignment data
    @GetMapping("/create")
    public Map<String, Object> showCreateForm() {
        Map<String, Object> response = new HashMap<>();
        response.put("assignment", new CouponAssignmentModel());
        response.put("assignmentTypes", CouponAssignmentModel.AssignmentType.values());
        response.put("assignmentStatuses", CouponAssignmentModel.AssignmentStatus.values());
        response.put("coupons", couponService.getAllCoupons());
        response.put("restaurants", restaurantService.GetData());
        response.put("menuItems", menuService.GetData());
        response.put("pageTitle", "Create New Coupon Assignment");
        return response;
    }

    // Save new assignment
    @PostMapping("/store")
    public ResponseEntity<?> storeAssignment(@Valid @RequestBody CouponAssignmentModel assignment,
                                  BindingResult result) {

        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }

        try {
            couponAssignmentService.createAssignment(assignment);
            Map<String, String> response = new HashMap<>();
            response.put("success", "Coupon assignment created successfully!");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Show edit assignment data
    @GetMapping("/edit/{id}")
    public ResponseEntity<?> showEditForm(@PathVariable Long id) {
        var assignmentOpt = couponAssignmentService.getAssignmentById(id);

        if (assignmentOpt.isPresent()) {
            CouponAssignmentModel assignment = assignmentOpt.get();
            Map<String, Object> response = new HashMap<>();
            response.put("assignment", assignment);
            response.put("assignmentTypes", CouponAssignmentModel.AssignmentType.values());
            response.put("assignmentStatuses", CouponAssignmentModel.AssignmentStatus.values());
            response.put("coupons", couponService.getAllCoupons());
            response.put("restaurants", restaurantService.GetData());
            
            // Handle pre-filling for menu item assignments
            if (assignment.getAssignmentType() == CouponAssignmentModel.AssignmentType.MENU_ITEM && assignment.getMenuItem() != null) {
                Integer restaurantId = assignment.getMenuItem().getRestaurants().getResId();
                response.put("selectedRestaurantId", restaurantId);
                response.put("menuItems", menuService.getMenuItemsByRestaurant(restaurantId));
            } else {
                response.put("menuItems", menuService.GetData());
            }
            
            response.put("pageTitle", "Edit Coupon Assignment");
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Assignment not found");
            return ResponseEntity.status(404).body(response);
        }
    }

    // Update assignment
    @PostMapping("/update/{id}")
    public ResponseEntity<?> updateAssignment(@PathVariable Long id,
                                   @Valid @RequestBody CouponAssignmentModel assignment,
                                   BindingResult result) {

        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }

        try {
            couponAssignmentService.updateAssignment(id, assignment);
            Map<String, String> response = new HashMap<>();
            response.put("success", "Coupon assignment updated successfully!");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Delete assignment
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteAssignment(@PathVariable Long id) {
        try {
            couponAssignmentService.deleteAssignment(id);
            Map<String, String> response = new HashMap<>();
            response.put("success", "Coupon assignment deleted successfully!");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // Toggle assignment status
    @PostMapping("/toggle/{id}")
    public ResponseEntity<?> toggleAssignmentStatus(@PathVariable Long id) {
        try {
            couponAssignmentService.toggleAssignmentStatus(id);
            Map<String, String> response = new HashMap<>();
            response.put("success", "Assignment status updated successfully!");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    // View assignment details
    @GetMapping("/view/{id}")
    public ResponseEntity<?> viewAssignment(@PathVariable Long id) {
        var assignmentOpt = couponAssignmentService.getAssignmentById(id);

        if (assignmentOpt.isPresent()) {
            return ResponseEntity.ok(assignmentOpt.get());
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Assignment not found");
            return ResponseEntity.status(404).body(response);
        }
    }

    // API endpoint for getting assignments by coupon
    @GetMapping("/api/by-coupon/{couponId}")
    public List<CouponAssignmentModel> getAssignmentsByCoupon(@PathVariable Long couponId) {
        return couponAssignmentService.getAssignmentsByCoupon(couponId);
    }

    // API endpoint for getting assignments by restaurant
    @GetMapping("/api/by-restaurant/{restaurantId}")
    public List<CouponAssignmentModel> getAssignmentsByRestaurant(@PathVariable Integer restaurantId) {
        return couponAssignmentService.getAssignmentsByRestaurant(restaurantId);
    }

    // API endpoint for getting assignments by menu item
    @GetMapping("/api/by-menu-item/{menuItemId}")
    public List<CouponAssignmentModel> getAssignmentsByMenuItem(@PathVariable Integer menuItemId) {
        return couponAssignmentService.getAssignmentsByMenuItem(menuItemId);
    }

    // API endpoint for getting menu items by restaurant
    @GetMapping("/api/menu-items/by-restaurant/{restaurantId}")
    public List<MenuModel> getMenuItemsByRestaurant(@PathVariable Integer restaurantId) {
        return menuService.getMenuItemsByRestaurant(restaurantId);
    }
}
