package com.example.food_delivery_app.models;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Data
@Entity
@Table(name = "sale_detail")
public class SaleDetailModel {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "detail_id")
    private int id;
    private String itemCode;
    private String itemDesc;
    private int qty;
    private double salePrice;
    private double total = qty * salePrice;
    private boolean status;
    private LocalDate createdAt;
    @ManyToOne
    @JoinColumn(name = "header_id")
    @JsonIgnore
    private SaleHeaderModel saleHeader;
    @ManyToOne
    @JoinColumn(name = "coupon_id")
    private CouponModel coupon;
    @ManyToOne
    @JoinColumn(name = "res_id")
    private RestaurantModel restaurant;
}
