package com.example.food_delivery_app.backend_admin.controllers;

import com.example.food_delivery_app.backend_admin.interfaces.ICategoryService;
import com.example.food_delivery_app.dto.repsonse.ApiResponse;
import com.example.food_delivery_app.models.CategoryModel;
import com.example.food_delivery_app.shared.constants.ApiRoutes;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(ApiRoutes.ADMIN_CATEGORIES)
@AllArgsConstructor
public class CategoryAdminController {
    private final ICategoryService categoryService;
    @GetMapping
    public ResponseEntity<?> index() {
        var data = categoryService.GetData();
        return ResponseEntity.ok(ApiResponse.builder().
                success(true).message("fetch categories success").data(data).build());
    }

    @GetMapping("/create")
    public Map<String, Object> create() {
        Map<String, Object> response = new HashMap<>();
        response.put("category", new CategoryModel());
        response.put("pageTitle", "Add New Category");
        return response;
    }

    @GetMapping("/edit/{id}")
    public ResponseEntity<?> edit(@PathVariable("id") int id) {
        CategoryModel category = categoryService.findById(id);
        if (category == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Category not found");
            return ResponseEntity.status(404).body(error);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("category", category);
        response.put("pageTitle", "Edit Category");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/store")
    public ResponseEntity<?> store(@RequestBody CategoryModel category) {
        if (category.getCateId() > 0) {
            categoryService.UpdateData(category);
        } else {
            categoryService.AddData(category);
        }
        
        Map<String, String> response = new HashMap<>();
        response.put("success", "Category saved successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable("id") int id) {
        CategoryModel category = categoryService.findById(id);
        Map<String, String> response = new HashMap<>();
        if (category != null) {
            categoryService.Delete(category);
            response.put("success", "Category deleted successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "Category not found");
            return ResponseEntity.status(404).body(response);
        }
    }
}
