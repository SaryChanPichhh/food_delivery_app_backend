package com.example.food_delivery_app.controllers.backends;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/backend/settings")
public class SettingsController {

    @GetMapping("")
    public Map<String, Object> index() {
        Map<String, Object> response = new HashMap<>();
        response.put("pageTitle", "Settings");
        return response;
    }
}
