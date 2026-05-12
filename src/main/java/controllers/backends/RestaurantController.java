package com.group_one.food_delivery_app.controllers.backends;

import com.group_one.food_delivery_app.models.RestaurantModel;
import com.group_one.food_delivery_app.repositories.RestaurantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/backend/restaurants")
public class RestaurantController {

    private final RestaurantRepository restaurantRepository;

    @Autowired
    public RestaurantController(RestaurantRepository restaurantRepository) {
        this.restaurantRepository = restaurantRepository;
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("restaurant", new RestaurantModel());
        model.addAttribute("pageTitle", "បន្ថែមភោជនីយដ្ឋានថ្មី");
        return "backend/restaurant/create";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable("id") int id, Model model) {
        RestaurantModel restaurant = restaurantRepository.findById(id).orElse(null);
        if (restaurant == null) {
            return "redirect:/backend/restaurants";
        }
        model.addAttribute("restaurant", restaurant);
        model.addAttribute("pageTitle", "កែសម្រួលភោជនីយដ្ឋាន");
        return "backend/restaurant/edit";
    }

    @PostMapping("/store")
    public String store(@ModelAttribute("restaurant") RestaurantModel restaurant) {
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
        return "redirect:/backend/restaurants";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") int id) {
        restaurantRepository.deleteById(id);
        return "redirect:/backend/restaurants";
    }

    @GetMapping
    public String index(Model model) {
        List<RestaurantModel> restaurants = restaurantRepository.findAll();
        model.addAttribute("restaurants", restaurants);
        return "backend/restaurant/index";
    }
}
