package com.backend.dto;

import com.backend.entity.Order;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class OrderResponse {
    public Long id;
    public String name;
    public String phone;
    public String email;
    public String address;
    public String city;
    public String state;
    public String pin;
    public String deliveryType;
    public Double subtotal;
    public Double deliveryFee;
    public Double total;
    public String status;
    public LocalDateTime createdAt;
    public List<OrderItemResponse> items;

    public static OrderResponse from(Order o) {
        OrderResponse r = new OrderResponse();
        r.id = o.getId();
        r.name = o.getName();
        r.phone = o.getPhone();
        r.email = o.getEmail();
        r.address = o.getAddress();
        r.city = o.getCity();
        r.state = o.getState();
        r.pin = o.getPin();
        r.deliveryType = o.getDeliveryType();
        r.subtotal = o.getSubtotal();
        r.deliveryFee = o.getDeliveryFee();
        r.total = o.getTotal();
        r.status = o.getStatus();
        r.createdAt = o.getCreatedAt();
        r.items = o.getItems().stream().map(OrderItemResponse::from).collect(Collectors.toList());
        return r;
    }
}
