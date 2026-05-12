package com.group_one.food_delivery_app.controllers.backends;

import com.group_one.food_delivery_app.Interfaces.IUserService;
import com.group_one.food_delivery_app.models.UserModel;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/backend/settings/permissions")
@AllArgsConstructor
public class PermissionController {

    private final IUserService userService;

    @GetMapping("")
    public String index(Model model) {
        List<UserModel> users = userService.GetData();
        model.addAttribute("users", users);
        model.addAttribute("pageTitle", "Role & Permission Management");
        return "backend/settings/permissions/index";
    }

    @PostMapping("/update-role/{userId}")
    public String updateRole(@PathVariable int userId, @RequestParam String role, RedirectAttributes redirectAttributes) {
        UserModel user = userService.GetData().stream()
                .filter(u -> u.getId() == userId)
                .findFirst()
                .orElse(null);
        
        if (user != null) {
            user.setRole(role);
            userService.UpdateData(user);
            redirectAttributes.addFlashAttribute("success", "Role updated for user: " + user.getUserName());
        } else {
            redirectAttributes.addFlashAttribute("error", "User not found.");
        }
        return "redirect:/backend/settings/permissions";
    }
}
