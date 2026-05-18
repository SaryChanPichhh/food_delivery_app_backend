package com.example.food_delivery_app.backend_admin.controllers;

import com.example.food_delivery_app.backend_admin.interfaces.IMenuService;
import com.example.food_delivery_app.models.MenuModel;
import com.example.food_delivery_app.repositories.CategoryRepository;
import com.example.food_delivery_app.repositories.MenuRepository;
import com.example.food_delivery_app.repositories.RestaurantRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/backend/menu")
@AllArgsConstructor
public class MenuAdminController {
    private final IMenuService menuService;
    private final MenuRepository menuRepository;
    private final RestaurantRepository restaurantRepository;
    private final CategoryRepository categoryRepository;

    @GetMapping
    public Map<String, Object> index() {
        var menus = menuService.GetData();
        Map<String, Object> response = new HashMap<>();
        response.put("menus", menus);
        return response;
    }

    @GetMapping("/create")
    public Map<String, Object> create() {
        Map<String, Object> response = new HashMap<>();
        response.put("menu", new MenuModel());
        response.put("restaurants", restaurantRepository.findAll());
        response.put("categories", categoryRepository.findAll());
        response.put("pageTitle", "Add New Menu Item");
        return response;
    }

    @GetMapping("/edit/{id}")
    public ResponseEntity<?> edit(@PathVariable("id") int id) {
        MenuModel menu = menuRepository.findById(id).orElse(null);
        if (menu == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Menu item not found");
            return ResponseEntity.status(404).body(error);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("menu", menu);
        response.put("restaurants", restaurantRepository.findAll());
        response.put("categories", categoryRepository.findAll());
        response.put("pageTitle", "Edit Menu Item");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/store")
    public ResponseEntity<?> store(@RequestParam("name") String name,
            @RequestParam("price") double price,
            @RequestParam("description") String description,
            @RequestParam("image") String image,
            @RequestParam("quantity") int quantity,
            @RequestParam("restaurantId") int restaurantId,
            @RequestParam(value = "categoryId", required = false) Integer categoryId,
            @RequestParam(value = "id", required = false) Integer id) {
        MenuModel menu;
        if (id != null && id > 0) {
            menu = menuRepository.findById(id).orElse(new MenuModel());
        } else {
            menu = new MenuModel();
        }
        menu.setName(name);
        menu.setPrice(price);
        menu.setDescription(description);
        menu.setImage(image);
        menu.setRestaurants(restaurantRepository.findById(restaurantId).orElse(null));
        if (categoryId != null) {
            menu.setCategories(categoryRepository.findById(categoryId).orElse(null));
        }
        menuRepository.save(menu);

        Map<String, String> response = new HashMap<>();
        response.put("success", "Menu item saved successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") int id) {
        menuRepository.deleteById(id);
        Map<String, String> response = new HashMap<>();
        response.put("success", "Menu item deleted successfully");
        return ResponseEntity.ok(response);
    }
}
