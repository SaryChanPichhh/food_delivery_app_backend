package com.group_one.food_delivery_app.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

import com.group_one.food_delivery_app.utils.enums.PaymentMethod;

@Data
@Entity
@Table(name = "sale_header")
public class SaleHeaderModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "header_id")
    private int id;
    
    private double total;
    private String invoiceType; // ORDER,PAID
    private boolean status;
    private LocalDate createdAt;
    @ManyToOne
    @JoinColumn(name = "user_id")
    private UserModel user;

    @ManyToOne
    @JoinColumn(name = "delivery_id")
    private DeliveryModel delivery;
    
    @OneToMany(mappedBy = "saleHeader")
    private List<SaleDetailModel> saleDetails;
    
    private String paymentMethod;
    private double commissionAmount;
    private double exchangeRate;

    @ManyToOne
    @JoinColumn(name = "exchange_rate_id")
    private ExchangeRateModel exchangeRateModel;
}
