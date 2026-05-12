package com.group_one.food_delivery_app.controllers.backends;

import com.group_one.food_delivery_app.models.CouponAssignmentModel;
import com.group_one.food_delivery_app.models.MenuModel;
import com.group_one.food_delivery_app.services.CouponAssignmentService;
import com.group_one.food_delivery_app.services.CouponService;
import com.group_one.food_delivery_app.services.MenuService;
import com.group_one.food_delivery_app.services.RestaurantService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
@Controller
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
    public String listAssignments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String keyword,
            Model model) {

        Page<CouponAssignmentModel> assignmentPage = couponAssignmentService.getAllAssignments(page, size, keyword);

        model.addAttribute("assignmentPage", assignmentPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", assignmentPage.getTotalPages());
        model.addAttribute("totalItems", assignmentPage.getTotalElements());

        // Add statistics
        model.addAttribute("stats", couponAssignmentService.getAssignmentStatistics());

        return "backend/coupon-assignment/index";
    }

    // Show create assignment form
    @GetMapping("/create")
    public String showCreateForm(Model model) {
        model.addAttribute("assignment", new CouponAssignmentModel());
        model.addAttribute("assignmentTypes", CouponAssignmentModel.AssignmentType.values());
        model.addAttribute("assignmentStatuses", CouponAssignmentModel.AssignmentStatus.values());
        model.addAttribute("coupons", couponService.getAllCoupons());
        model.addAttribute("restaurants", restaurantService.GetData());
        model.addAttribute("menuItems", menuService.GetData());
        model.addAttribute("pageTitle", "Create New Coupon Assignment");
        model.addAttribute("formAction", "/backend/coupon-assignments/store");
        return "backend/coupon-assignment/create";
    }

    // Save new assignment
    @PostMapping("/store")
    public String storeAssignment(@Valid @ModelAttribute("assignment") CouponAssignmentModel assignment,
                                  BindingResult result,
                                  Model model,
                                  RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("assignmentTypes", CouponAssignmentModel.AssignmentType.values());
            model.addAttribute("assignmentStatuses", CouponAssignmentModel.AssignmentStatus.values());
            model.addAttribute("coupons", couponService.getAllCoupons());
            model.addAttribute("restaurants", restaurantService.GetData());
            model.addAttribute("menuItems", menuService.GetData());
            model.addAttribute("pageTitle", "Create New Coupon Assignment");
            model.addAttribute("formAction", "/backend/coupon-assignments/store");
            return "backend/coupon-assignment/create";
        }

        try {
            couponAssignmentService.createAssignment(assignment);
            redirectAttributes.addFlashAttribute("success", "Coupon assignment created successfully!");
            return "redirect:/backend/coupon-assignments";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/backend/coupon-assignments/create";
        }
    }

    // Show edit assignment form
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        var assignmentOpt = couponAssignmentService.getAssignmentById(id);

        if (assignmentOpt.isPresent()) {
            CouponAssignmentModel assignment = assignmentOpt.get();
            model.addAttribute("assignment", assignment);
            model.addAttribute("assignmentTypes", CouponAssignmentModel.AssignmentType.values());
            model.addAttribute("assignmentStatuses", CouponAssignmentModel.AssignmentStatus.values());
            model.addAttribute("coupons", couponService.getAllCoupons());
            model.addAttribute("restaurants", restaurantService.GetData());
            
            // Handle pre-filling for menu item assignments
            if (assignment.getAssignmentType() == CouponAssignmentModel.AssignmentType.MENU_ITEM && assignment.getMenuItem() != null) {
                Integer restaurantId = assignment.getMenuItem().getRestaurants().getResId();
                model.addAttribute("selectedRestaurantId", restaurantId);
                model.addAttribute("menuItems", menuService.getMenuItemsByRestaurant(restaurantId));
            } else {
                model.addAttribute("menuItems", menuService.GetData());
            }
            
            model.addAttribute("pageTitle", "Edit Coupon Assignment");
            model.addAttribute("formAction", "/backend/coupon-assignments/update/" + id);
            return "backend/coupon-assignment/create";
        } else {
            return "redirect:/backend/coupon-assignments";
        }
    }

    // Update assignment
    @PostMapping("/update/{id}")
    public String updateAssignment(@PathVariable Long id,
                                   @Valid @ModelAttribute("assignment") CouponAssignmentModel assignment,
                                   BindingResult result,
                                   Model model,
                                   RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("assignmentTypes", CouponAssignmentModel.AssignmentType.values());
            model.addAttribute("assignmentStatuses", CouponAssignmentModel.AssignmentStatus.values());
            model.addAttribute("coupons", couponService.getAllCoupons());
            model.addAttribute("restaurants", restaurantService.GetData());
            model.addAttribute("menuItems", menuService.GetData());
            model.addAttribute("pageTitle", "Edit Coupon Assignment");
            model.addAttribute("formAction", "/backend/coupon-assignments/update/" + id);
            return "backend/coupon-assignment/create";
        }

        try {
            couponAssignmentService.updateAssignment(id, assignment);
            redirectAttributes.addFlashAttribute("success", "Coupon assignment updated successfully!");
            return "redirect:/backend/coupon-assignments";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/backend/coupon-assignments/edit/" + id;
        }
    }

    // Delete assignment
    @GetMapping("/delete/{id}")
    public String deleteAssignment(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            couponAssignmentService.deleteAssignment(id);
            redirectAttributes.addFlashAttribute("success", "Coupon assignment deleted successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/backend/coupon-assignments";
    }

    // Toggle assignment status
    @GetMapping("/toggle/{id}")
    public String toggleAssignmentStatus(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            couponAssignmentService.toggleAssignmentStatus(id);
            redirectAttributes.addFlashAttribute("success", "Assignment status updated successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/backend/coupon-assignments";
    }

    // View assignment details
    @GetMapping("/view/{id}")
    public String viewAssignment(@PathVariable Long id, Model model) {
        var assignmentOpt = couponAssignmentService.getAssignmentById(id);

        if (assignmentOpt.isPresent()) {
            model.addAttribute("assignment", assignmentOpt.get());
            return "backend/coupon-assignment/view";
        } else {
            return "redirect:/backend/coupon-assignments";
        }
    }

    // API endpoint for getting assignments by coupon
    @GetMapping("/api/by-coupon/{couponId}")
    @ResponseBody
    public List<CouponAssignmentModel> getAssignmentsByCoupon(@PathVariable Long couponId) {
        return couponAssignmentService.getAssignmentsByCoupon(couponId);
    }

    // API endpoint for getting assignments by restaurant
    @GetMapping("/api/by-restaurant/{restaurantId}")
    @ResponseBody
    public List<CouponAssignmentModel> getAssignmentsByRestaurant(@PathVariable Integer restaurantId) {
        return couponAssignmentService.getAssignmentsByRestaurant(restaurantId);
    }

    // API endpoint for getting assignments by menu item
    @GetMapping("/api/by-menu-item/{menuItemId}")
    @ResponseBody
    public List<CouponAssignmentModel> getAssignmentsByMenuItem(@PathVariable Integer menuItemId) {
        return couponAssignmentService.getAssignmentsByMenuItem(menuItemId);
    }

    // API endpoint for getting menu items by restaurant
    @GetMapping("/api/menu-items/by-restaurant/{restaurantId}")
    @ResponseBody
    public List<MenuModel> getMenuItemsByRestaurant(@PathVariable Integer restaurantId) {
        return menuService.getMenuItemsByRestaurant(restaurantId);
    }
}
