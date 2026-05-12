package com.group_one.food_delivery_app.controllers;

import com.group_one.food_delivery_app.models.FeedbackModel;
import com.group_one.food_delivery_app.models.UserModel;
import com.group_one.food_delivery_app.repositories.FaqRepository;
import com.group_one.food_delivery_app.repositories.FeedbackRepository;
import com.group_one.food_delivery_app.repositories.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/faq")
@RequiredArgsConstructor
public class FaqController {

    private final FaqRepository faqRepository;
    private final FeedbackRepository feedbackRepository;
    private final UserRepository userRepository;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("faqs", faqRepository.findAllByActiveTrueOrderBySortOrderAsc());
        return "faq/index";
    }

    @PostMapping("/feedback")
    public String submitFeedback(@RequestParam String subject,
                                 @RequestParam String message,
                                 HttpSession session,
                                 RedirectAttributes redirectAttributes) {
        Object userIdObj = session.getAttribute("userId");
        if (userIdObj == null) {
            redirectAttributes.addFlashAttribute("error", "សូមចូលក្នុងគណនីដើម្បីបញ្ចេញមតិយោបល់។");
            return "redirect:/auth/login";
        }

        int userId = (int) userIdObj;
        UserModel user = userRepository.findById(userId).orElse(null);

        FeedbackModel feedback = new FeedbackModel();
        feedback.setUser(user);
        feedback.setSubject(subject);
        feedback.setMessage(message);
        
        feedbackRepository.save(feedback);

        redirectAttributes.addFlashAttribute("success", "សូមអរគុណសម្រាប់ការបញ្ចេញមតិយោបល់របស់អ្នក!");
        return "redirect:/faq";
    }
}
