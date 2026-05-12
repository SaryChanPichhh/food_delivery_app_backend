package com.group_one.food_delivery_app.controllers.backends;

import com.group_one.food_delivery_app.Interfaces.IUserService;
import com.group_one.food_delivery_app.models.UserModel;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Controller
@RequestMapping("/backend/settings/profile")
@AllArgsConstructor
public class ProfileController {

    private final IUserService userService;

    @GetMapping("")
    public String index(Model model) {
        // As security is not fully implemented, we'll act as the first user (Admin)
        UserModel user = userService.GetData().stream().findFirst().orElse(new UserModel());
        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "My Profile");
        return "backend/settings/profile";
    }

    @PostMapping("/update")
    public String updateProfile(@ModelAttribute("user") UserModel user, 
                                @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                                RedirectAttributes redirectAttributes) {
        try {
            if (imageFile != null && !imageFile.isEmpty()) {
                String uploadDir = "src/main/resources/static/assets/images/users/";
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                String fileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(imageFile.getInputStream(), filePath);

                user.setProfileImage(fileName);
            } else {
                // Keep existing image
                UserModel existingUser = userService.GetData().stream()
                        .filter(u -> u.getId() == user.getId())
                        .findFirst()
                        .orElse(null);
                if (existingUser != null) {
                    user.setProfileImage(existingUser.getProfileImage());
                }
            }

            userService.UpdateData(user);
            redirectAttributes.addFlashAttribute("success", "Profile updated successfully!");
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "Error uploading image: " + e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating profile: " + e.getMessage());
        }
        return "redirect:/backend/settings/profile";
    }
}
