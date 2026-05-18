package com.example.food_delivery_app.backend_user.controllers;

import com.example.food_delivery_app.backend_admin.interfaces.ICategoryService;
import com.example.food_delivery_app.dto.repsonse.ApiResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/categories")
public class CategoryController {
    private final ICategoryService _category;

    @GetMapping("")
    public ResponseEntity<?> index(Authentication authentication){
        var getCate = _category.GetData();
        var res = ApiResponse.builder().success(true).message("fetch category success")
                .data(getCate).build();
        return ResponseEntity.ok(
                res
        );
    }
}
