package com.example.food_delivery_app.backend_admin.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {
    @GetMapping("/api/test")
    public String test() {
        return "JWT Protected API";
    }
}
