package com.group_one.food_delivery_app.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/pickup")
public class PickupController {
    @GetMapping("")
    public String index(Model model){
        model.addAttribute("activePage", "pickup");
        return "pickup/index";
    }
}
