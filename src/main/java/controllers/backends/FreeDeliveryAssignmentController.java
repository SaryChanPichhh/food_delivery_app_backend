package com.group_one.food_delivery_app.controllers.backends;

import com.group_one.food_delivery_app.models.FreeDeliveryAssignmentModel;
import com.group_one.food_delivery_app.models.MenuModel;
import com.group_one.food_delivery_app.services.FreeDeliveryAssignmentService;
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
@RequestMapping("/backend/free-delivery")
public class FreeDeliveryAssignmentController {

    @Autowired
    private FreeDeliveryAssignmentService freeDeliveryService;

    @Autowired
    private RestaurantService restaurantService;

    @Autowired
    private MenuService menuService;

    @GetMapping("")
    public String index(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "") String keyword,
            Model model) {

        Page<FreeDeliveryAssignmentModel> assignmentPage = freeDeliveryService.getAllAssignments(page, size, keyword);
        model.addAttribute("assignmentPage", assignmentPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", assignmentPage.getTotalPages());
        model.addAttribute("totalItems", assignmentPage.getTotalElements());
        model.addAttribute("stats", freeDeliveryService.getStats());

        return "backend/free-delivery/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("assignment", new FreeDeliveryAssignmentModel());
        model.addAttribute("assignmentTypes", FreeDeliveryAssignmentModel.AssignmentType.values());
        model.addAttribute("assignmentStatuses", FreeDeliveryAssignmentModel.AssignmentStatus.values());
        model.addAttribute("restaurants", restaurantService.GetData());
        model.addAttribute("pageTitle", "Assign Free Delivery");
        model.addAttribute("formAction", "/backend/free-delivery/store");
        return "backend/free-delivery/create";
    }

    @PostMapping("/store")
    public String store(@Valid @ModelAttribute("assignment") FreeDeliveryAssignmentModel assignment,
                        BindingResult result,
                        Model model,
                        RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("assignmentTypes", FreeDeliveryAssignmentModel.AssignmentType.values());
            model.addAttribute("assignmentStatuses", FreeDeliveryAssignmentModel.AssignmentStatus.values());
            model.addAttribute("restaurants", restaurantService.GetData());
            model.addAttribute("pageTitle", "Assign Free Delivery");
            model.addAttribute("formAction", "/backend/free-delivery/store");
            return "backend/free-delivery/create";
        }

        try {
            freeDeliveryService.createAssignment(assignment);
            redirectAttributes.addFlashAttribute("success", "Free delivery assignment created successfully!");
            return "redirect:/backend/free-delivery";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/backend/free-delivery/create";
        }
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        var assignmentOpt = freeDeliveryService.getAssignmentById(id);
        if (assignmentOpt.isPresent()) {
            FreeDeliveryAssignmentModel assignment = assignmentOpt.get();
            model.addAttribute("assignment", assignment);
            model.addAttribute("assignmentTypes", FreeDeliveryAssignmentModel.AssignmentType.values());
            model.addAttribute("assignmentStatuses", FreeDeliveryAssignmentModel.AssignmentStatus.values());
            model.addAttribute("restaurants", restaurantService.GetData());
            
            if (assignment.getAssignmentType() == FreeDeliveryAssignmentModel.AssignmentType.MENU_ITEM && assignment.getMenuItem() != null) {
                Integer restaurantId = assignment.getMenuItem().getRestaurants().getResId();
                model.addAttribute("selectedRestaurantId", restaurantId);
                model.addAttribute("menuItems", menuService.getMenuItemsByRestaurant(restaurantId));
            }

            model.addAttribute("pageTitle", "Edit Free Delivery Assignment");
            model.addAttribute("formAction", "/backend/free-delivery/update/" + id);
            return "backend/free-delivery/create";
        }
        return "redirect:/backend/free-delivery";
    }

    @PostMapping("/update/{id}")
    public String update(@PathVariable Long id,
                         @Valid @ModelAttribute("assignment") FreeDeliveryAssignmentModel assignment,
                         BindingResult result,
                         Model model,
                         RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            model.addAttribute("assignmentTypes", FreeDeliveryAssignmentModel.AssignmentType.values());
            model.addAttribute("assignmentStatuses", FreeDeliveryAssignmentModel.AssignmentStatus.values());
            model.addAttribute("restaurants", restaurantService.GetData());
            model.addAttribute("pageTitle", "Edit Free Delivery Assignment");
            model.addAttribute("formAction", "/backend/free-delivery/update/" + id);
            return "backend/free-delivery/create";
        }

        try {
            freeDeliveryService.updateAssignment(id, assignment);
            redirectAttributes.addFlashAttribute("success", "Free delivery assignment updated successfully!");
            return "redirect:/backend/free-delivery";
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
            return "redirect:/backend/free-delivery/edit/" + id;
        }
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            freeDeliveryService.deleteAssignment(id);
            redirectAttributes.addFlashAttribute("success", "Free delivery assignment deleted successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/backend/free-delivery";
    }

    @GetMapping("/toggle/{id}")
    public String toggle(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            freeDeliveryService.toggleStatus(id);
            redirectAttributes.addFlashAttribute("success", "Status updated successfully!");
        } catch (RuntimeException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/backend/free-delivery";
    }

    @GetMapping("/view/{id}")
    public String view(@PathVariable Long id, Model model) {
        var assignmentOpt = freeDeliveryService.getAssignmentById(id);
        if (assignmentOpt.isPresent()) {
            model.addAttribute("assignment", assignmentOpt.get());
            return "backend/free-delivery/view";
        }
        return "redirect:/backend/free-delivery";
    }

    @GetMapping("/api/menu-items/by-restaurant/{restaurantId}")
    @ResponseBody
    public List<MenuModel> getMenuItemsByRestaurant(@PathVariable Integer restaurantId) {
        return menuService.getMenuItemsByRestaurant(restaurantId);
    }
}
