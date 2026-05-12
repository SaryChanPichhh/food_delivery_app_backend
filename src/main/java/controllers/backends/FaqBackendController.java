package com.group_one.food_delivery_app.controllers.backends;

import com.group_one.food_delivery_app.models.FaqModel;
import com.group_one.food_delivery_app.models.FeedbackModel;
import com.group_one.food_delivery_app.repositories.FaqRepository;
import com.group_one.food_delivery_app.repositories.FeedbackRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/backend/faqs")
@RequiredArgsConstructor
public class FaqBackendController {

    private final FaqRepository faqRepository;
    private final FeedbackRepository feedbackRepository;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("faqs", faqRepository.findAll());
        model.addAttribute("feedbacks", feedbackRepository.findAll());
        model.addAttribute("pageTitle", "គ្រប់គ្រងសំណួរ និងមតិយោបល់");
        return "backend/faq/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("faq", new FaqModel());
        model.addAttribute("pageTitle", "បន្ថែមសំណួរថ្មី");
        return "backend/faq/create";
    }

    @PostMapping("/store")
    public String store(@ModelAttribute FaqModel faq, RedirectAttributes redirectAttributes) {
        faqRepository.save(faq);
        redirectAttributes.addFlashAttribute("success", "រក្សាទុកដោយជោគជ័យ!");
        return "redirect:/backend/faqs";
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        FaqModel faq = faqRepository.findById(id).orElseThrow();
        model.addAttribute("faq", faq);
        model.addAttribute("pageTitle", "កែសម្រួលសំណួរ");
        return "backend/faq/edit";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        faqRepository.deleteById(id);
        return "redirect:/backend/faqs";
    }

    @GetMapping("/feedback/delete/{id}")
    public String deleteFeedback(@PathVariable Long id) {
        feedbackRepository.deleteById(id);
        return "redirect:/backend/faqs";
    }
}
