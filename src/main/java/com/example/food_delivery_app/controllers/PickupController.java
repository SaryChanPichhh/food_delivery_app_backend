package com.example.food_delivery_app.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/pickup")
public class PickupController {
    @GetMapping("")
    public Map<String, Object> index() {
        Map<String, Object> response = new HashMap<>();
        response.put("activePage", "pickup");
        return response;
    }
}
