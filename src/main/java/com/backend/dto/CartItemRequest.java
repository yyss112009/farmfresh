package com.backend.dto;

public class CartItemRequest {
    public Long productId;
    public String productName; // fallback snapshot if productId is missing/stale
    public Double price;
    public Integer quantity;
    public String unit;
}
