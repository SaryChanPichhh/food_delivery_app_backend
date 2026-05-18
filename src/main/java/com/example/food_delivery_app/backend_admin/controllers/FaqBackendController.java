package com.example.food_delivery_app.backend_admin.controllers;

import com.example.food_delivery_app.models.FaqModel;
import com.example.food_delivery_app.repositories.FaqRepository;
import com.example.food_delivery_app.repositories.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/backend/faqs")
@RequiredArgsConstructor
public class FaqBackendController {

    private final FaqRepository faqRepository;
    private final FeedbackRepository feedbackRepository;

    @GetMapping
    public Map<String, Object> index() {
        Map<String, Object> response = new HashMap<>();
        response.put("faqs", faqRepository.findAll());
        response.put("feedbacks", feedbackRepository.findAll());
        response.put("pageTitle", "គ្រប់គ្រងសំណួរ និងមតិយោបល់");
        return response;
    }

    @GetMapping("/create")
    public Map<String, Object> create() {
        Map<String, Object> response = new HashMap<>();
        response.put("faq", new FaqModel());
        response.put("pageTitle", "បន្ថែមសំណួរថ្មី");
        return response;
    }

    @PostMapping("/store")
    public ResponseEntity<?> store(@RequestBody FaqModel faq) {
        faqRepository.save(faq);
        Map<String, String> response = new HashMap<>();
        response.put("success", "រក្សាទុកដោយជោគជ័យ!");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/edit/{id}")
    public ResponseEntity<?> edit(@PathVariable Long id) {
        FaqModel faq = faqRepository.findById(id).orElse(null);
        if (faq == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "FAQ not found");
            return ResponseEntity.status(404).body(error);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("faq", faq);
        response.put("pageTitle", "កែសម្រួលសំណួរ");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        faqRepository.deleteById(id);
        Map<String, String> response = new HashMap<>();
        response.put("success", "FAQ deleted successfully");
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/feedback/delete/{id}")
    public ResponseEntity<?> deleteFeedback(@PathVariable Long id) {
        feedbackRepository.deleteById(id);
        Map<String, String> response = new HashMap<>();
        response.put("success", "Feedback deleted successfully");
        return ResponseEntity.ok(response);
    }
}
