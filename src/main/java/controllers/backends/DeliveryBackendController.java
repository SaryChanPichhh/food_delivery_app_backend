package com.group_one.food_delivery_app.controllers.backends;

import com.group_one.food_delivery_app.models.DeliveryModel;
import com.group_one.food_delivery_app.repositories.DeliveryRepository;
import com.group_one.food_delivery_app.repositories.UserRepository;
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

@Controller
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
    public String index(Model model) {
        List<DeliveryModel> deliveries = deliveryRepository.findAll();
        model.addAttribute("deliveries", deliveries);
        return "backend/delivery/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("delivery", new DeliveryModel());
        var deliveryUsers = userRepository.findAll().stream()
                .filter(u -> "USER".equalsIgnoreCase(u.getRole()))
                .toList();
        model.addAttribute("users", deliveryUsers);
        model.addAttribute("pageTitle", "បន្ថែមអ្នកដឹកជញ្ជូនថ្មី");
        return "backend/delivery/create";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") Long id, Model model) {
        DeliveryModel delivery = deliveryRepository.findById(id).orElse(null);
        if (delivery == null) {
            return "redirect:/backend/deliveries";
        }
        model.addAttribute("delivery", delivery);
        var deliveryUsers = userRepository.findAll().stream()
                .filter(u -> "USER".equalsIgnoreCase(u.getRole()))
                .toList();
        model.addAttribute("users", deliveryUsers);
        model.addAttribute("pageTitle", "កែសម្រួលអ្នកដឹកជញ្ជូន");
        return "backend/delivery/edit";
    }

    @PostMapping("/store")
    public String store(@ModelAttribute("delivery") DeliveryModel delivery, 
                        @RequestParam("userId") int userId,
                        @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
                        @RequestParam(value = "idFile", required = false) MultipartFile idFile,
                        @RequestParam(value = "licenseFile", required = false) MultipartFile licenseFile,
                        RedirectAttributes redirectAttributes) {
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
            redirectAttributes.addFlashAttribute("success", "រក្សាទុកអ្នកដឹកជញ្ជូនដោយជោគជ័យ!");
            return "redirect:/backend/deliveries";
        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("error", "មានកំហុសក្នុងការរក្សាទុកឯកសារ: " + e.getMessage());
            return "redirect:/backend/deliveries/create";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "មានកំហុសក្នុងការរក្សាទុក: " + e.getMessage());
            return "redirect:/backend/deliveries/create";
        }
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id, RedirectAttributes redirectAttributes) {
        try {
            deliveryRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "លុបអ្នកដឹកជញ្ជូនដោយជោគជ័យ!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "មានកំហុសក្នុងការលុប: " + e.getMessage());
        }
        return "redirect:/backend/deliveries";
    }
}
