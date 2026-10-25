package com.backend.dto;

import java.util.ArrayList;
import java.util.List;

public class AdminOrderRow {
    public Long id;
    public String customerName;
    public String farmerName;
    public String phone;
    public String email;
    public String address;
    public String city;
    public String state;
    public String pin;
    public String deliveryType;
    public Double deliveryFee;
    public Double subtotal;
    public double amount;
    public String status;
    public String createdAt;
    public List<OrderItemResponse> items = new ArrayList<>();
}
