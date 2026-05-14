package com.example.food_delivery_app.controllers;

import com.example.food_delivery_app.models.FeedbackModel;
import com.example.food_delivery_app.models.UserModel;
import com.example.food_delivery_app.repositories.FaqRepository;
import com.example.food_delivery_app.repositories.FeedbackRepository;
import com.example.food_delivery_app.repositories.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/faq")
@RequiredArgsConstructor
public class FaqController {

    private final FaqRepository faqRepository;
    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;

    @GetMapping
    public Map<String, Object> index() {
        Map<String, Object> response = new HashMap<>();
        response.put("faqs", faqRepository.findAllByActiveTrueOrderBySortOrderAsc());
        return response;
    }

    @PostMapping("/feedback")
    public ResponseEntity<?> submitFeedback(@RequestParam String subject,
                                 @RequestParam String message,
                                 HttpSession session) {
        Object userIdObj = session.getAttribute("userId");
        Map<String, String> response = new HashMap<>();
        if (userIdObj == null) {
            response.put("error", "សូមចូលក្នុងគណនីដើម្បីបញ្ចេញមតិយោបល់។");
            return ResponseEntity.status(401).body(response);
        }

        int userId = (int) userIdObj;
        UserModel user = userRepository.findById(userId).orElse(null);

        FeedbackModel feedback = new FeedbackModel();
        feedback.setUser(user);
        feedback.setSubject(subject);
        feedback.setMessage(message);
        
        feedbackRepository.save(feedback);

        response.put("success", "សូមអរគុណសម្រាប់ការបញ្ចេញមតិយោបល់របស់អ្នក!");
        return ResponseEntity.ok(response);
    }
}
