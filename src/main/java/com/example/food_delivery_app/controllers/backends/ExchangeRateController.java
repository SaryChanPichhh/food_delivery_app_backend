package com.example.food_delivery_app.controllers.backends;

import com.example.food_delivery_app.models.ExchangeRateModel;
import com.example.food_delivery_app.services.ExchangeRateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/backend/settings/exchange-rates")
public class ExchangeRateController {

    @Autowired
    private ExchangeRateService exchangeRateService;

    @GetMapping("")
    public Map<String, Object> index() {
        List<ExchangeRateModel> rates = exchangeRateService.getAllRates();
        Map<String, Object> response = new HashMap<>();
        response.put("rates", rates);
        response.put("pageTitle", "អត្រាប្តូរប្រាក់");
        return response;
    }

    @GetMapping("/create")
    public Map<String, Object> create() {
        Map<String, Object> response = new HashMap<>();
        response.put("exchangeRate", new ExchangeRateModel());
        response.put("pageTitle", "បន្ថែមអត្រាប្តូរប្រាក់ថ្មី");
        return response;
    }

    @PostMapping("/store")
    public ResponseEntity<?> store(@RequestBody ExchangeRateModel rate) {
        Map<String, String> response = new HashMap<>();
        try {
            exchangeRateService.saveRate(rate);
            response.put("success", "រក្សាទុកអត្រាប្តូរប្រាក់ដោយជោគជ័យ!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "មានកំហុសក្នុងការរក្សាទុកអត្រាប្តូរប្រាក់: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @GetMapping("/edit/{id}")
    public ResponseEntity<?> edit(@PathVariable Long id) {
        Optional<ExchangeRateModel> rateOpt = exchangeRateService.getRateById(id);
        if (rateOpt.isPresent()) {
            Map<String, Object> response = new HashMap<>();
            response.put("exchangeRate", rateOpt.get());
            response.put("pageTitle", "កែសម្រួលអត្រាប្តូរប្រាក់");
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Exchange rate not found");
            return ResponseEntity.status(404).body(error);
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        Map<String, String> response = new HashMap<>();
        try {
            exchangeRateService.deleteRate(id);
            response.put("success", "លុបអត្រាប្តូរប្រាក់ដោយជោគជ័យ!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "មានកំហុសក្នុងការលុបអត្រាប្តូរប្រាក់: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }

    @PostMapping("/set-default/{id}")
    public ResponseEntity<?> setDefault(@PathVariable Long id) {
        Map<String, String> response = new HashMap<>();
        try {
            exchangeRateService.setAsDefault(id);
            response.put("success", "កំណត់អត្រាប្តូរប្រាក់លំនាំដើមដោយជោគជ័យ!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("error", "មានកំហុសក្នុងការកំណត់អត្រាប្តូរប្រាក់លំនាំដើម: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}
