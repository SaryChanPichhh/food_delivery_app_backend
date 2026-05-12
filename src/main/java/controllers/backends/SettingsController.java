package com.group_one.food_delivery_app.controllers.backends;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/backend/settings")
public class SettingsController {

    @GetMapping("")
    public String index(Model model) {
        model.addAttribute("pageTitle", "Settings");
        return "backend/settings/index";
    }
}
