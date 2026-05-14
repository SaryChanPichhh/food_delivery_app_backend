package com.example.food_delivery_app.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.food_delivery_app.Interfaces.ISaleService;
import com.example.food_delivery_app.dtos.repsonse.ViewHistOrderDto;

import com.example.food_delivery_app.models.SaleHeaderModel;
import com.example.food_delivery_app.models.SaleDetailModel;
import lombok.AllArgsConstructor;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/checkout")
@AllArgsConstructor
public class CheckOutController {
    private final ISaleService saleService;

    @GetMapping("")
    public ResponseEntity<?> viewHistOrder(jakarta.servlet.http.HttpSession session) {
        Object userIdObj = session.getAttribute("userId");
        if (userIdObj == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
        int userId = (int) userIdObj;
        var saleOrderHis = saleService.getAllSaleOrderByUserId(userId);
        var viewHistOrderDtos = saleOrderHis.stream()
            .map((SaleHeaderModel x) -> {
                ViewHistOrderDto dto = new ViewHistOrderDto();
                dto.setInvoiceType(x.getInvoiceType());
                dto.setInvoiceNo(String.valueOf(x.getId()));
                dto.setInvoiceDate(x.getCreatedAt() != null ? x.getCreatedAt().toString() : null);
                dto.setInvoiceStatus(x.isStatus() ? "ACTIVE" : "INACTIVE");
                dto.setInvoiceTotalPrice(x.getTotal());
                
                int totalQty = x.getSaleDetails() != null
                        ? x.getSaleDetails().stream().mapToInt((SaleDetailModel d) -> d.getQty()).sum()
                        : 0;
                dto.setInvoiceTotalQuantity(totalQty);
                dto.setPaymentMethod(x.getPaymentMethod() != null ? x.getPaymentMethod() : "CASH");
                dto.setExchangeRate(x.getExchangeRate());
                
                return dto;
            })
            .toList();
            
        var inOrder = viewHistOrderDtos.stream().filter(x -> "ORDER".equals(x.getInvoiceType())).toList();
        var inPaid = viewHistOrderDtos.stream().filter(x -> "PAID".equals(x.getInvoiceType())).toList();
        
        Map<String, Object> response = new HashMap<>();
        response.put("inOrder", inOrder);
        response.put("inPaid", inPaid);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/invoice-detail/{id}")
    public ResponseEntity<?> getInvoiceDetail(@PathVariable int id, jakarta.servlet.http.HttpSession session) {
        Object userIdObj = session.getAttribute("userId");
        if (userIdObj == null) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Unauthorized");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
        int userId = (int) userIdObj;
        var saleOrderHis = saleService.getSaleOrderByUserIdAndId(userId, id);
        
        Map<String, Object> response = new HashMap<>();
        response.put("currentOrder", saleOrderHis);
        return ResponseEntity.ok(response);
    }
}
