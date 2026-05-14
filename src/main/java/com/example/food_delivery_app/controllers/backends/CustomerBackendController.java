package com.example.food_delivery_app.controllers.backends;

import com.example.food_delivery_app.Interfaces.IUserService;
import com.example.food_delivery_app.models.UserModel;
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

import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/backend/customers")
@AllArgsConstructor
public class CustomerBackendController {

    private final IUserService userService;

    @GetMapping
    public Map<String, Object> index() {
        List<UserModel> customers = userService.GetData().stream()
                .filter(u -> "CUST".equalsIgnoreCase(u.getRole()))
                .toList();
        Map<String, Object> response = new HashMap<>();
        response.put("users", customers);
        response.put("pageTitle", "គ្រប់គ្រងអតិថិជន");
        return response;
    }

    @GetMapping("/create")
    public Map<String, Object> create() {
        UserModel user = new UserModel();
        user.setRole("CUST");
        Map<String, Object> response = new HashMap<>();
        response.put("user", user);
        response.put("pageTitle", "បន្ថែមអតិថិជនថ្មី");
        return response;
    }

    @PostMapping("/store")
    public ResponseEntity<?> store(@RequestBody UserModel user, @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {
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
        
        Map<String, String> response = new HashMap<>();
        response.put("success", "Customer saved successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/edit/{id}")
    public ResponseEntity<?> edit(@PathVariable("id") int id) {
        UserModel user = userService.findById(id);
        if (user == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Customer not found");
            return ResponseEntity.status(404).body(error);
        }
        
        user.setPassword(""); 
        Map<String, Object> response = new HashMap<>();
        response.put("user", user);
        response.put("pageTitle", "កែសម្រួលព័ត៌មានអតិថិជន");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") int id) {
        UserModel user = userService.findById(id);
        Map<String, String> response = new HashMap<>();
        if (user != null) {
            userService.Delete(user);
            response.put("success", "Customer deleted successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "Customer not found");
            return ResponseEntity.status(404).body(response);
        }
    }
}
