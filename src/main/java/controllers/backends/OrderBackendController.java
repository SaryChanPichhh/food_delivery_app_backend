package com.group_one.food_delivery_app.controllers.backends;

import com.group_one.food_delivery_app.models.SaleDetailModel;
import com.group_one.food_delivery_app.models.SaleHeaderModel;
import com.group_one.food_delivery_app.models.RestaurantModel;
import com.group_one.food_delivery_app.repositories.SaleRepository;
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

@Controller
@RequestMapping("/backend/orders")
@AllArgsConstructor
public class OrderBackendController {

    private final SaleRepository saleRepository;

    @GetMapping("/today")
    public String todayOrders(Model model) {
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

        model.addAttribute("ordersByRestaurant", ordersByRestaurant);
        model.addAttribute("today", today);
        model.addAttribute("totalInvoices", todaySales.size());
        model.addAttribute("totalRevenue", todaySales.stream()
                .filter(s -> "PAID".equalsIgnoreCase(s.getInvoiceType()))
                .mapToDouble(SaleHeaderModel::getTotal)
                .sum());

        return "backend/orders/today";
    }

    @PostMapping("/update-status")
    public String updateStatus(@RequestParam int id, @RequestParam String status) {
        SaleHeaderModel sale = saleRepository.findById(id).orElse(null);
        if (sale != null) {
            sale.setInvoiceType(status);
            saleRepository.save(sale);
        }
        return "redirect:/backend/orders/today";
    }
}
