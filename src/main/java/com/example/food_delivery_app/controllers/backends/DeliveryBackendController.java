package com.example.food_delivery_app.controllers.backends;

import com.example.food_delivery_app.models.DeliveryModel;
import com.example.food_delivery_app.repositories.DeliveryRepository;
import com.example.food_delivery_app.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/backend/deliveries")
public class DeliveryBackendController {

    private final DeliveryRepository deliveryRepository;
    private final UserRepository userRepository;

    @Autowired
    public DeliveryBackendController(DeliveryRepository deliveryRepository, UserRepository userRepository) {
        this.deliveryRepository = deliveryRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public Map<String, Object> index() {
        List<DeliveryModel> deliveries = deliveryRepository.findAll();
        Map<String, Object> response = new HashMap<>();
        response.put("deliveries", deliveries);
        return response;
    }

    @GetMapping("/create")
    public Map<String, Object> create() {
        Map<String, Object> response = new HashMap<>();
        response.put("delivery", new DeliveryModel());
        var deliveryUsers = userRepository.findAll().stream()
                .filter(u -> "USER".equalsIgnoreCase(u.getRole()))
                .toList();
        response.put("users", deliveryUsers);
        response.put("pageTitle", "បន្ថែមអ្នកដឹកជញ្ជូនថ្មី");
        return response;
    }

    @GetMapping("/edit/{id}")
    public ResponseEntity<?> edit(@PathVariable("id") Long id) {
        DeliveryModel delivery = deliveryRepository.findById(id).orElse(null);
        if (delivery == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Delivery personnel not found");
            return ResponseEntity.status(404).body(error);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("delivery", delivery);
        var deliveryUsers = userRepository.findAll().stream()
                .filter(u -> "USER".equalsIgnoreCase(u.getRole()))
                .toList();
        response.put("users", deliveryUsers);
        response.put("pageTitle", "កែសម្រួលអ្នកដឹកជញ្ជូន");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/store")
    public ResponseEntity<?> store(@RequestBody DeliveryModel delivery, 
                        @RequestParam("userId") int userId,
                        @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                        @RequestParam(value = "idFile", required = false) MultipartFile idFile,
                        @RequestParam(value = "licenseFile", required = false) MultipartFile licenseFile) {
        Map<String, String> response = new HashMap<>();
        try {
            // Handle existing data if editing
            if (delivery.getId() != null) {
                DeliveryModel existing = deliveryRepository.findById(delivery.getId()).orElse(null);
                if (existing != null) {
                    delivery.setImageUrl(existing.getImageUrl());
                    delivery.setNationalIdUrl(existing.getNationalIdUrl());
                    delivery.setDriverLicenseUrl(existing.getDriverLicenseUrl());
                }
            }

            // File upload directory
            String uploadDir = "src/main/resources/static/uploads/deliveries/";
            Path uploadPath = Paths.get(uploadDir);
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // Upload Profile Image
            if (imageFile != null && !imageFile.isEmpty()) {
                String fileName = UUID.randomUUID().toString() + "_" + imageFile.getOriginalFilename();
                Files.copy(imageFile.getInputStream(), uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
                delivery.setImageUrl(fileName);
            }

            // Upload ID Card
            if (idFile != null && !idFile.isEmpty()) {
                String fileName = UUID.randomUUID().toString() + "_" + idFile.getOriginalFilename();
                Files.copy(idFile.getInputStream(), uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
                delivery.setNationalIdUrl(fileName);
            }

            // Upload License
            if (licenseFile != null && !licenseFile.isEmpty()) {
                String fileName = UUID.randomUUID().toString() + "_" + licenseFile.getOriginalFilename();
                Files.copy(licenseFile.getInputStream(), uploadPath.resolve(fileName), StandardCopyOption.REPLACE_EXISTING);
                delivery.setDriverLicenseUrl(fileName);
            }

            var user = userRepository.findById(userId).orElse(null);
            delivery.setUsers(user);
            deliveryRepository.save(delivery);
            response.put("success", "រក្សាទុកអ្នកដឹកជញ្ជូនដោយជោគជ័យ!");
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            response.put("error", "មានកំហុសក្នុងការរក្សាទុកឯកសារ: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        } catch (Exception e) {
            response.put("error", "មានកំហុសក្នុងការរក្សាទុក: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") Long id) {
        Map<String, String> response = new HashMap<>();
        try {
            deliveryRepository.deleteById(id);
            response.put("success", "លុបអ្នកដឹកជញ្ជូនដោយជោគជ័យ!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "មានកំហុសក្នុងការលុប: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
