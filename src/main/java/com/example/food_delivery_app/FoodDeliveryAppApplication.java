package com.example.food_delivery_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class FoodDeliveryAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(FoodDeliveryAppApplication.class, args);
    }

}
