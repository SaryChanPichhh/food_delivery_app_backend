package com.group_one.food_delivery_app.controllers;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.group_one.food_delivery_app.Interfaces.IFavoritesService;
import com.group_one.food_delivery_app.models.FavoritesModel;

import lombok.AllArgsConstructor;

import java.util.List;

@Controller
@RequestMapping("favorites")
@AllArgsConstructor
public class FavoritesController {
    private final ObjectMapper mapper = new ObjectMapper()
            .enable(SerializationFeature.INDENT_OUTPUT)
            .findAndRegisterModules();

    private final IFavoritesService favoritesService;
    @GetMapping(name = "/")
    public String index(Model model,jakarta.servlet.http.HttpSession session) throws JsonProcessingException {
        Object userIdObj = session.getAttribute("userId");
        int userId = userIdObj != null ? (int) userIdObj : 1;
        List<FavoritesModel> favorites = favoritesService.findByUserId((long) userId);
    var favoritesRes = favorites.stream()
            .filter(x -> x.getRestaurants() != null && x.getRestaurants().getResId() != 0).toList();
    var favoritesMenu = favorites.stream()
            .filter(x -> x.getMenu() != null && x.getMenu().getId() != 0)
    .toList();        
    model.addAttribute("favoritesRes", favoritesRes);
        model.addAttribute("favoritesMenu", favoritesMenu);
        String json = mapper.writeValueAsString(favoritesMenu);
        System.out.println("favortiesMenu: " + json);
    return "favorites/index";
    }

    @PostMapping("/toggle")
    @ResponseBody
    public String toggleFavorite(@RequestParam(required = false) Long resId, 
                                 @RequestParam(required = false) Long menuId,
                                 jakarta.servlet.http.HttpSession session) {
        Object userIdObj = session.getAttribute("userId");
        int userId = userIdObj != null ? (int) userIdObj : 1;
        
        FavoritesModel result = favoritesService.toggleFavorite((long) userId, resId, menuId);
        
        if (result != null) {
            return "{\"status\":\"added\"}";
        } else {
            return "{\"status\":\"removed\"}";
        }
    }
}
