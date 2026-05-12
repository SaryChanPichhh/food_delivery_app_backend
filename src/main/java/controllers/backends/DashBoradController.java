package com.group_one.food_delivery_app.controllers.backends;

import com.group_one.food_delivery_app.models.RestaurantModel;
import com.group_one.food_delivery_app.models.UserModel;
import com.group_one.food_delivery_app.models.SaleDetailModel;
import com.group_one.food_delivery_app.models.SaleHeaderModel;
import com.group_one.food_delivery_app.repositories.RestaurantRepository;
import com.group_one.food_delivery_app.repositories.SaleRepository;
import com.group_one.food_delivery_app.repositories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/backend/dashboard")
@AllArgsConstructor
public class DashBoradController {

    private final UserRepository userRepository;
    private final RestaurantRepository restaurantRepository;
    private final SaleRepository saleRepository;

    @GetMapping("")
    public String index(Model model, 
                        @RequestParam(required = false) String startDate, 
                        @RequestParam(required = false) String endDate) {
        // Fetch all data
        var allUsers = userRepository.findAll();
        var allRestaurants = restaurantRepository.findAll();
        var allSales = saleRepository.findAll();

        // Default to today if no dates provided
        LocalDate start = (startDate != null && !startDate.isEmpty()) ? LocalDate.parse(startDate) : LocalDate.now();
        LocalDate end = (endDate != null && !endDate.isEmpty()) ? LocalDate.parse(endDate) : LocalDate.now();

        // Basic Stats
        long totalUsers = allUsers.stream().filter(u -> "USER".equalsIgnoreCase(u.getRole())).count();
        long totalStaff = allUsers.stream().filter(u -> !"USER".equalsIgnoreCase(u.getRole())).count();
        long totalRes = allRestaurants.size();

        double totalSaleInRange = 0;
        double totalCommissionInRange = 0;
        long totalOrdersInRange = 0;

        for (SaleHeaderModel sale : allSales) {
            // Count all orders regardless of date
            if ("ORDER".equalsIgnoreCase(sale.getInvoiceType())) {
                totalOrdersInRange++;
            }

            LocalDate saleDate = sale.getCreatedAt();
            if (saleDate != null) {
                // Check if saleDate is within range [start, end] for PAID sales
                if (!saleDate.isBefore(start) && !saleDate.isAfter(end)) {
                    if ("PAID".equalsIgnoreCase(sale.getInvoiceType())) {
                        totalSaleInRange += sale.getTotal();
                        totalCommissionInRange += sale.getCommissionAmount();
                    }
                }
            }
        }

        // 1. Top 5 Users (By number of orders in range)
        var topUsers = allSales.stream()
                .filter(s -> s.getUser() != null && s.getCreatedAt() != null && !s.getCreatedAt().isBefore(start) && !s.getCreatedAt().isAfter(end))
                .collect(Collectors.groupingBy(SaleHeaderModel::getUser, Collectors.counting()))
                .entrySet().stream()
                .sorted(Map.Entry.<UserModel, Long>comparingByValue().reversed())
                .limit(5)
                .toList();

        // 2. Top 5 Restaurants (By items sold in range)
        var topRestaurants = allSales.stream()
                .filter(s -> s.getCreatedAt() != null && !s.getCreatedAt().isBefore(start) && !s.getCreatedAt().isAfter(end))
                .flatMap(s -> s.getSaleDetails().stream())
                .filter(d -> d.getRestaurant() != null)
                .collect(Collectors.groupingBy(SaleDetailModel::getRestaurant, Collectors.summingInt(SaleDetailModel::getQty)))
                .entrySet().stream()
                .sorted(Map.Entry.<RestaurantModel, Integer>comparingByValue().reversed())
                .limit(5)
                .toList();

        // 3. Top 5 Orders in Range (Latest first)
        var filteredOrders = allSales.stream()
                .filter(s -> s.getCreatedAt() != null && !s.getCreatedAt().isBefore(start) && !s.getCreatedAt().isAfter(end))
                .sorted(Comparator.comparing(SaleHeaderModel::getId).reversed())
                .limit(5)
                .toList();

        // 4. Top 5 Menu Items (By quantity sold in range)
        var topMenus = allSales.stream()
                .filter(s -> s.getCreatedAt() != null && !s.getCreatedAt().isBefore(start) && !s.getCreatedAt().isAfter(end))
                .flatMap(s -> s.getSaleDetails().stream())
                .collect(Collectors.groupingBy(SaleDetailModel::getItemDesc, Collectors.summingInt(SaleDetailModel::getQty)))
                .entrySet().stream()
                .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                .limit(5)
                .toList();

        // 5. Sales Data for Chart (Last 7 Days)
        List<String> chartLabels = new java.util.ArrayList<>();
        List<Double> chartData = new java.util.ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            chartLabels.add(date.getDayOfWeek().toString().substring(0, 3));
            double dayTotal = allSales.stream()
                    .filter(s -> s.getCreatedAt() != null && s.getCreatedAt().equals(date) && "PAID".equalsIgnoreCase(s.getInvoiceType()))
                    .mapToDouble(SaleHeaderModel::getTotal)
                    .sum();
            chartData.add(dayTotal);
        }

        // Add to model
        model.addAttribute("totalUser", totalUsers);
        model.addAttribute("totalStaff", totalStaff);
        model.addAttribute("totalRes", totalRes);
        model.addAttribute("totalSaleToday", totalSaleInRange);
        model.addAttribute("totalCommissionToday", totalCommissionInRange);
        model.addAttribute("totalOrders", totalOrdersInRange);
        
        model.addAttribute("topUsers", topUsers);
        model.addAttribute("topRestaurants", topRestaurants);
        model.addAttribute("todayOrders", filteredOrders);
        model.addAttribute("topMenus", topMenus);

        model.addAttribute("chartLabels", chartLabels);
        model.addAttribute("chartData", chartData);

        // Pass dates back to UI
        model.addAttribute("startDate", start);
        model.addAttribute("endDate", end);

        return "backend/dashbaord/index";
    }

}
