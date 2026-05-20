package com.example.food_delivery_app.backend_user.controllers;

import com.example.food_delivery_app.backend_user.interfaces.IMenuService;
import com.example.food_delivery_app.dto.repsonse.ApiResponse;
import com.example.food_delivery_app.shared.constants.ApiRoutes;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping(ApiRoutes.USER_MENUS)
@AllArgsConstructor
public class MenuController {
    private final IMenuService _menuService;

    @GetMapping("/new-menu")
    public ResponseEntity<?> getNewMenu(Authentication auth){
        Map<String, Object> user =
                (Map<String, Object>)
                        auth.getPrincipal();
        Integer userId =
                (Integer) user.get("userId");
        var data = _menuService.getNewMenus(userId);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true).message("fetch success").data(data).build());

    }
    @GetMapping("/getbyresid")
    public ResponseEntity<?> getMenuByResId(Authentication auth,@RequestParam int resId){
        Map<String, Object> user =
                (Map<String, Object>)
                        auth.getPrincipal();
        Integer userId =
                (Integer) user.get("userId");
        var data = _menuService.getMenuByResId(userId,resId);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true).message("fetch success").data(data).build());

    }
    @GetMapping("/popular-menu")
    public ResponseEntity<?> getPopularMenu(Authentication auth){
        Map<String, Object> user =
                (Map<String, Object>)
                        auth.getPrincipal();

        Integer userId =
                (Integer) user.get("userId");
        var data = _menuService.getNewMenus(userId);
        return ResponseEntity.ok(ApiResponse.builder()
                .success(true).message("fetch success").data(data).build());

    }
}
