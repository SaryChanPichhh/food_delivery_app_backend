package com.group_one.food_delivery_app.controllers;

import com.group_one.food_delivery_app.Interfaces.IReviewService;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/delivery/reviews")
@AllArgsConstructor
public class ReviewController {
    private final IReviewService reviewService;

    @PostMapping("/submit")
    public String submitReview(@RequestParam int resId,
                               @RequestParam int rating,
                               @RequestParam String comment,
                               HttpSession session,
                               RedirectAttributes redirectAttributes) {
        
        Object userIdObj = session.getAttribute("userId");
        if (userIdObj == null) {
            redirectAttributes.addFlashAttribute("error", "Please login to leave a review.");
            return "redirect:/login";
        }

        int userId = (int) userIdObj;
        
        try {
            reviewService.submitReview(userId, resId, rating, comment);
            redirectAttributes.addFlashAttribute("success", "Thank you for your review!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to submit review: " + e.getMessage());
        }

        return "redirect:/delivery/restuarant-detail?res_id=" + resId;
    }
}
