package com.backend.dto;

import com.backend.entity.OrderItem;

public class OrderItemResponse {
    public Long id;
    public Long productId;
    public String productName;
    public String farmerName;
    public Double price;
    public Integer quantity;
    public String unit;
    public Long orderId;
    public String customerName;
    public String customerPhone;
    public String customerEmail;
    public String deliveryAddress;
    public String city;
    public String state;
    public String pin;
    public String deliveryType;
    public Double lineTotal;
    public String status;
    public java.time.LocalDateTime createdAt;

    public static OrderItemResponse from(OrderItem i) {
        OrderItemResponse r = new OrderItemResponse();
        r.id = i.getId();
        r.productId = i.getProduct() != null ? i.getProduct().getId() : null;
        r.productName = i.getProductName();
        r.farmerName = i.getFarmerName();
        r.price = i.getPrice();
        r.quantity = i.getQuantity();
        r.unit = i.getUnit();
        r.lineTotal = (i.getPrice() != null ? i.getPrice() : 0.0) * (i.getQuantity() != null ? i.getQuantity() : 1);
        if (i.getOrder() != null) {
            r.orderId = i.getOrder().getId();
            r.customerName = i.getOrder().getName();
            r.customerPhone = i.getOrder().getPhone();
            r.customerEmail = i.getOrder().getEmail();
            r.deliveryAddress = i.getOrder().getAddress();
            r.city = i.getOrder().getCity();
            r.state = i.getOrder().getState();
            r.pin = i.getOrder().getPin();
            r.deliveryType = i.getOrder().getDeliveryType();
            r.status = i.getOrder().getStatus();
            r.createdAt = i.getOrder().getCreatedAt();
        }
        return r;
    }
}
