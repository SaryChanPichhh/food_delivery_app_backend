package com.example.food_delivery_app.controllers.backends;

import com.example.food_delivery_app.models.RestaurantModel;
import com.example.food_delivery_app.repositories.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/backend/restaurants")
public class RestaurantController {

    private final RestaurantRepository restaurantRepository;

    @Autowired
    public RestaurantController(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @GetMapping("/create")
    public Map<String, Object> create() {
        Map<String, Object> response = new HashMap<>();
        response.put("restaurant", new RestaurantModel());
        response.put("pageTitle", "បន្ថែមភោជនីយដ្ឋានថ្មី");
        return response;
    }

    @GetMapping("/edit/{id}")
    public ResponseEntity<?> edit(@PathVariable("id") int id) {
        RestaurantModel restaurant = restaurantRepository.findById(id).orElse(null);
        if (restaurant == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Restaurant not found");
            return ResponseEntity.status(404).body(error);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("restaurant", restaurant);
        response.put("pageTitle", "កែសម្រួលភោជនីយដ្ឋាន");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/store")
    public ResponseEntity<?> store(@RequestBody RestaurantModel restaurant) {
        if (restaurant.getResId() == 0) {
            restaurant.setCreatedAt(java.time.LocalDate.now());
        } else {
            // Preserve the original created date for updates if needed
            RestaurantModel existing = restaurantRepository.findById(restaurant.getResId()).orElse(null);
            if (existing != null) {
                restaurant.setCreatedAt(existing.getCreatedAt());
            }
        }
        restaurantRepository.save(restaurant);
        
        Map<String, String> response = new HashMap<>();
        response.put("success", "Restaurant saved successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") int id) {
        restaurantRepository.deleteById(id);
        Map<String, String> response = new HashMap<>();
        response.put("success", "Restaurant deleted successfully");
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public Map<String, Object> index() {
        List<RestaurantModel> restaurants = restaurantRepository.findAll();
        Map<String, Object> response = new HashMap<>();
        response.put("restaurants", restaurants);
        return response;
    }
}
