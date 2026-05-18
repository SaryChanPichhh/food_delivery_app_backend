package com.example.food_delivery_app.backend_user.controllers;

import com.example.food_delivery_app.backend_user.interfaces.IReviewService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/delivery/reviews")
@AllArgsConstructor
public class ReviewController {
    private final IReviewService reviewService;

    @PostMapping("/submit")
    public ResponseEntity<?> submitReview(@RequestParam int resId,
                                @RequestParam int rating,
                                @RequestParam String comment,
                                HttpSession session) {
        
        Object userIdObj = session.getAttribute("userId");
        Map<String, String> response = new HashMap<>();
        if (userIdObj == null) {
            response.put("error", "Please login to leave a review.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }

        int userId = (int) userIdObj;
        
        try {
            reviewService.submitReview(userId, resId, rating, comment);
            response.put("success", "Thank you for your review!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "Failed to submit review: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
}
