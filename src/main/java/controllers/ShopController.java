package com.group_one.food_delivery_app.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/shop")
public class ShopController {
    @GetMapping("")
    public String index(Model model){
        model.addAttribute("activePage", "shop");
        return "shop/index";
    }
}
