package com.example.food_delivery_app.backend_user.controllers;

import com.example.food_delivery_app.backend_user.interfaces.IFavoritesService;
import org.springframework.web.bind.annotation.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.example.food_delivery_app.models.FavoritesModel;
import lombok.AllArgsConstructor;
import java.util.List;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("favorites")
@AllArgsConstructor
public class FavoritesController {
    private final ObjectMapper mapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .findAndRegisterModules();

    private final IFavoritesService favoritesService;

    @GetMapping("/")
    public Map<String, Object> index(jakarta.servlet.http.HttpSession session) throws JsonProcessingException {
        Object userIdObj = session.getAttribute("userId");
        int userId = userIdObj != null ? (int) userIdObj : 1;
        List<FavoritesModel> favorites = favoritesService.findByUserId((long) userId);
        var favoritesRes = favorites.stream()
                .filter(x -> x.getRestaurants() != null && x.getRestaurants().getResId() != 0).toList();
        var favoritesMenu = favorites.stream()
                .filter(x -> x.getMenu() != null && x.getMenu().getId() != 0)
                .toList();
        
        Map<String, Object> response = new HashMap<>();
        response.put("favoritesRes", favoritesRes);
        response.put("favoritesMenu", favoritesMenu);
        
        String json = mapper.writeValueAsString(favoritesMenu);
        System.out.println("favortiesMenu: " + json);
        return response;
    }

    @PostMapping("/toggle")
    public Map<String, String> toggleFavorite(@RequestParam(required = false) Long resId,
            @RequestParam(required = false) Long menuId,
            jakarta.servlet.http.HttpSession session) {
        Object userIdObj = session.getAttribute("userId");
        int userId = userIdObj != null ? (int) userIdObj : 1;

        FavoritesModel result = favoritesService.toggleFavorite((long) userId, resId, menuId);

        Map<String, String> response = new HashMap<>();
        if (result != null) {
            response.put("status", "added");
        } else {
            response.put("status", "removed");
        }
        return response;
    }
}
