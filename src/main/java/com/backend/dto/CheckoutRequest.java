package com.backend.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class CheckoutRequest {
    @NotBlank public String name;
    @NotBlank public String phone;
    @NotBlank public String email;
    @NotBlank public String address;
    public String city;
    public String state;
    public String pin;
    public String deliveryType;
    public Double deliveryFee = 0.0;

    @NotEmpty
    @Valid
    public List<CartItemRequest> items;
}
