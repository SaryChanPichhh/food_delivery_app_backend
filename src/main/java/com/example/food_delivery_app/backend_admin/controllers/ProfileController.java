package com.example.food_delivery_app.backend_admin.controllers;

import com.example.food_delivery_app.backend_admin.interfaces.IUserService;
import com.example.food_delivery_app.models.UserModel;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/backend/settings/profile")
@AllArgsConstructor
public class ProfileController {

    private final IUserService userService;

    @GetMapping("")
    public Map<String, Object> index() {
        // As security is not fully implemented, we'll act as the first user (Admin)
        UserModel user = userService.GetData().stream().findFirst().orElse(new UserModel());
        Map<String, Object> response = new HashMap<>();
        response.put("user", user);
        response.put("pageTitle", "My Profile");
        return response;
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateProfile(@RequestBody UserModel user, 
                                 @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
        Map<String, String> response = new HashMap<>();
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
            response.put("success", "Profile updated successfully!");
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("error", "Error uploading image: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        } catch (Exception e) {
            response.put("error", "Error updating profile: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
