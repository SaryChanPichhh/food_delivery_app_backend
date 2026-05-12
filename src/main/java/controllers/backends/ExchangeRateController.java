package com.group_one.food_delivery_app.controllers.backends;

import com.group_one.food_delivery_app.models.ExchangeRateModel;
import com.group_one.food_delivery_app.services.ExchangeRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/backend/settings/exchange-rates")
public class ExchangeRateController {

    @Autowired
    private ExchangeRateService exchangeRateService;

    @GetMapping("")
    public String index(Model model) {
        List<ExchangeRateModel> rates = exchangeRateService.getAllRates();
        model.addAttribute("rates", rates);
        model.addAttribute("pageTitle", "អត្រាប្តូរប្រាក់");
        return "backend/settings/exchange-rates/index";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("exchangeRate", new ExchangeRateModel());
        model.addAttribute("pageTitle", "បន្ថែមអត្រាប្តូរប្រាក់ថ្មី");
        return "backend/settings/exchange-rates/create";
    }

    @PostMapping("/store")
    public String store(@ModelAttribute("exchangeRate") ExchangeRateModel rate, RedirectAttributes redirectAttributes) {
        try {
            exchangeRateService.saveRate(rate);
            redirectAttributes.addFlashAttribute("success", "រក្សាទុកអត្រាប្តូរប្រាក់ដោយជោគជ័យ!");
            return "redirect:/backend/settings/exchange-rates";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "មានកំហុសក្នុងការរក្សាទុកអត្រាប្តូរប្រាក់: " + e.getMessage());
            return "redirect:/backend/settings/exchange-rates/create";
        }
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable Long id, Model model) {
        Optional<ExchangeRateModel> rateOpt = exchangeRateService.getRateById(id);
        if (rateOpt.isPresent()) {
            model.addAttribute("exchangeRate", rateOpt.get());
            model.addAttribute("pageTitle", "កែសម្រួលអត្រាប្តូរប្រាក់");
            return "backend/settings/exchange-rates/create"; // reusing the create view
        }
        return "redirect:/backend/settings/exchange-rates";
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            exchangeRateService.deleteRate(id);
            redirectAttributes.addFlashAttribute("success", "លុបអត្រាប្តូរប្រាក់ដោយជោគជ័យ!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "មានកំហុសក្នុងការលុបអត្រាប្តូរប្រាក់: " + e.getMessage());
        }
        return "redirect:/backend/settings/exchange-rates";
    }

    @GetMapping("/set-default/{id}")
    public String setDefault(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            exchangeRateService.setAsDefault(id);
            redirectAttributes.addFlashAttribute("success", "កំណត់អត្រាប្តូរប្រាក់លំនាំដើមដោយជោគជ័យ!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "មានកំហុសក្នុងការកំណត់អត្រាប្តូរប្រាក់លំនាំដើម: " + e.getMessage());
        }
        return "redirect:/backend/settings/exchange-rates";
    }
}
