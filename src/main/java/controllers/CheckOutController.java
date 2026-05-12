package com.group_one.food_delivery_app.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.group_one.food_delivery_app.Interfaces.ISaleService;
import com.group_one.food_delivery_app.dtos.repsonse.ViewHistOrderDto;
import com.group_one.food_delivery_app.utils.helper.Convertor;

import com.group_one.food_delivery_app.models.SaleHeaderModel;
import com.group_one.food_delivery_app.models.SaleDetailModel;
import lombok.AllArgsConstructor;

@Controller
@RequestMapping("/checkout")
@AllArgsConstructor
public class CheckOutController {
    private final ISaleService saleService;

    @RequestMapping("")
    public String viewHistOrder(jakarta.servlet.http.HttpSession session, Model model) {
        Object userIdObj = session.getAttribute("userId");
        if (userIdObj == null) {
            return "redirect:/auth/login";
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
        model.addAttribute("inOrder", inOrder);
        model.addAttribute("inPaid", inPaid);
        return "checkout/index";
    }

    @RequestMapping("/invoice-detail/{id}")
    public String getInvoiceDetail(@PathVariable int id, jakarta.servlet.http.HttpSession session, Model model) {
        Object userIdObj = session.getAttribute("userId");
        if (userIdObj == null) {
            return "redirect:/auth/login";
        }
        int userId = (int) userIdObj;
        var saleOrderHis = saleService.getSaleOrderByUserIdAndId(userId, id);
        model.addAttribute("currentOrder", saleOrderHis);
        return "checkout/invoice-detail";
    }
}
