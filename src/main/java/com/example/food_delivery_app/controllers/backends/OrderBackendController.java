package com.example.food_delivery_app.controllers.backends;

import com.example.food_delivery_app.models.SaleDetailModel;
import com.example.food_delivery_app.models.SaleHeaderModel;
import com.example.food_delivery_app.models.RestaurantModel;
import com.example.food_delivery_app.repositories.SaleRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import java.util.HashMap;

@RestController
@RequestMapping("/backend/orders")
@AllArgsConstructor
public class OrderBackendController {

    private final SaleRepository saleRepository;

    @GetMapping("/today")
    public Map<String, Object> todayOrders() {
        LocalDate today = LocalDate.now();
        List<SaleHeaderModel> allSales = saleRepository.findAll();

        // Filter for today's PAID and ORDER invoices
        List<SaleHeaderModel> todaySales = allSales.stream()
                .filter(s -> s.getCreatedAt() != null && s.getCreatedAt().equals(today))
                .toList();

        // Group SaleDetails by Restaurant
        Map<RestaurantModel, List<SaleDetailModel>> ordersByRestaurant = todaySales.stream()
                .flatMap(s -> s.getSaleDetails().stream())
                .filter(d -> d.getRestaurant() != null)
                .collect(Collectors.groupingBy(SaleDetailModel::getRestaurant));

        Map<String, Object> response = new HashMap<>();
        response.put("ordersByRestaurant", ordersByRestaurant);
        response.put("today", today);
        response.put("totalInvoices", todaySales.size());
        response.put("totalRevenue", todaySales.stream()
                .filter(s -> "PAID".equalsIgnoreCase(s.getInvoiceType()))
                .mapToDouble(SaleHeaderModel::getTotal)
                .sum());

        return response;
    }

    @PostMapping("/update-status")
    public ResponseEntity<?> updateStatus(@RequestParam int id, @RequestParam String status) {
        SaleHeaderModel sale = saleRepository.findById(id).orElse(null);
        Map<String, String> response = new HashMap<>();
        if (sale != null) {
            sale.setInvoiceType(status);
            saleRepository.save(sale);
            response.put("success", "Status updated successfully");
            return ResponseEntity.ok(response);
        } else {
            response.put("error", "Order not found");
            return ResponseEntity.status(404).body(response);
        }
    }
}
