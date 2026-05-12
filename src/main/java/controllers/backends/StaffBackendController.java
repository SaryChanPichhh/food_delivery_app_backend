package com.group_one.food_delivery_app.controllers.backends;

import com.group_one.food_delivery_app.Interfaces.IUserService;
import com.group_one.food_delivery_app.models.UserModel;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/backend/staff")
@AllArgsConstructor
public class StaffBackendController {

    private final IUserService userService;

    @GetMapping
    public String index(Model model) {
        List<UserModel> staff = userService.GetData().stream()
                .filter(u -> !"USER".equalsIgnoreCase(u.getRole()) && !"CUST".equalsIgnoreCase(u.getRole()) && !"CUSTOMER".equalsIgnoreCase(u.getRole()))
                .toList();
        model.addAttribute("users", staff);
        model.addAttribute("pageTitle", "គ្រប់គ្រងបុគ្គលិក");
        return "backend/staff/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        UserModel user = new UserModel();
        user.setRole("ADMIN"); // Default role for staff creation
        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "បន្ថែមបុគ្គលិកថ្មី");
        return "backend/staff/create";
    }

    @PostMapping("/store")
    public String store(@ModelAttribute("user") UserModel user, @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        if (imageFile != null && !imageFile.isEmpty()) {
            try {
                String uploadDir = "src/main/resources/static/assets/images/users/";
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                String fileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
                Path filePath = uploadPath.resolve(fileName);
                Files.copy(imageFile.getInputStream(), filePath);

                user.setProfileImage(fileName);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (user.getId() != 0) {
            UserModel existingUser = userService.findById(user.getId());
            if (existingUser != null) {
                user.setProfileImage(existingUser.getProfileImage());
            }
        }

        if (user.getId() == 0) {
            userService.AddData(user);
        } else {
            userService.UpdateData(user);
        }
        
        return "redirect:/backend/staff";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") int id, Model model) {
        UserModel user = userService.findById(id);
        if (user == null) return "redirect:/backend/staff";
        
        user.setPassword(""); 
        model.addAttribute("user", user);
        model.addAttribute("pageTitle", "កែសម្រួលព័ត៌មានបុគ្គលិក");
        return "backend/staff/edit";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") int id) {
        UserModel user = userService.findById(id);
        if (user != null) {
            userService.Delete(user);
        }
        return "redirect:/backend/staff";
    }
}
