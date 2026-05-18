package com.example.food_delivery_app.backend_admin.controllers;

import com.example.food_delivery_app.backend_admin.services.AdminFreeDeliveryAssignmentService;
import com.example.food_delivery_app.backend_admin.services.AdminMenuService;
import com.example.food_delivery_app.backend_admin.services.AdminRestaurantService;
import com.example.food_delivery_app.dto.repsonse.MenuResponseDto;
import com.example.food_delivery_app.models.FreeDeliveryAssignmentModel;
import com.example.food_delivery_app.models.MenuModel;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/backend/free-delivery")
public class FreeDeliveryAssignmentController {

    @Autowired
    private AdminFreeDeliveryAssignmentService freeDeliveryService;

    @Autowired
    private AdminRestaurantService restaurantService;

    @Autowired
    private AdminMenuService menuService;

    @GetMapping("")
    public Map<String, Object> index(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String keyword) {

        Page<FreeDeliveryAssignmentModel> assignmentPage = freeDeliveryService.getAllAssignments(page, size, keyword);
        Map<String, Object> response = new HashMap<>();
        response.put("assignmentPage", assignmentPage);
        response.put("keyword", keyword);
        response.put("currentPage", page);
        response.put("totalPages", assignmentPage.getTotalPages());
        response.put("totalItems", assignmentPage.getTotalElements());
        response.put("stats", freeDeliveryService.getStats());

        return response;
    }

    @GetMapping("/create")
    public Map<String, Object> create() {
        Map<String, Object> response = new HashMap<>();
        response.put("assignment", new FreeDeliveryAssignmentModel());
        response.put("assignmentTypes", FreeDeliveryAssignmentModel.AssignmentType.values());
        response.put("assignmentStatuses", FreeDeliveryAssignmentModel.AssignmentStatus.values());
        response.put("restaurants", restaurantService.GetData());
        response.put("pageTitle", "Assign Free Delivery");
        return response;
    }

    @PostMapping("/store")
    public ResponseEntity<?> store(@Valid @RequestBody FreeDeliveryAssignmentModel assignment,
                        BindingResult result) {

        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }

        try {
            freeDeliveryService.createAssignment(assignment);
            Map<String, String> response = new HashMap<>();
            response.put("success", "Free delivery assignment created successfully!");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/edit/{id}")
    public ResponseEntity<?> edit(@PathVariable Long id) {
        var assignmentOpt = freeDeliveryService.getAssignmentById(id);
        if (assignmentOpt.isPresent()) {
            FreeDeliveryAssignmentModel assignment = assignmentOpt.get();
            Map<String, Object> response = new HashMap<>();
            response.put("assignment", assignment);
            response.put("assignmentTypes", FreeDeliveryAssignmentModel.AssignmentType.values());
            response.put("assignmentStatuses", FreeDeliveryAssignmentModel.AssignmentStatus.values());
            response.put("restaurants", restaurantService.GetData());
            
            if (assignment.getAssignmentType() == FreeDeliveryAssignmentModel.AssignmentType.MENU_ITEM && assignment.getMenuItem() != null) {
                Integer restaurantId = assignment.getMenuItem().getRestaurants().getResId();
                response.put("selectedRestaurantId", restaurantId);
                response.put("menuItems", menuService.getMenuItemsByRestaurant(1,restaurantId));
            }

            response.put("pageTitle", "Edit Free Delivery Assignment");
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Assignment not found");
            return ResponseEntity.status(404).body(response);
        }
    }

    @PostMapping("/update/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                         @Valid @RequestBody FreeDeliveryAssignmentModel assignment,
                         BindingResult result) {

        if (result.hasErrors()) {
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }

        try {
            freeDeliveryService.updateAssignment(id, assignment);
            Map<String, String> response = new HashMap<>();
            response.put("success", "Free delivery assignment updated successfully!");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            freeDeliveryService.deleteAssignment(id);
            Map<String, String> response = new HashMap<>();
            response.put("success", "Free delivery assignment deleted successfully!");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/toggle/{id}")
    public ResponseEntity<?> toggle(@PathVariable Long id) {
        try {
            freeDeliveryService.toggleStatus(id);
            Map<String, String> response = new HashMap<>();
            response.put("success", "Status updated successfully!");
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            Map<String, String> response = new HashMap<>();
            response.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/view/{id}")
    public ResponseEntity<?> view(@PathVariable Long id) {
        var assignmentOpt = freeDeliveryService.getAssignmentById(id);
        if (assignmentOpt.isPresent()) {
            return ResponseEntity.ok(assignmentOpt.get());
        } else {
            Map<String, String> response = new HashMap<>();
            response.put("error", "Assignment not found");
            return ResponseEntity.status(404).body(response);
        }
    }

    @GetMapping("/api/menu-items/by-restaurant/{restaurantId}")
    public List<MenuResponseDto> getMenuItemsByRestaurant(@PathVariable Integer restaurantId) {
        return menuService.getMenuItemsByRestaurant(1,restaurantId);
    }
}
