package com.backend.controller;

import com.backend.dto.CheckoutRequest;
import com.backend.dto.OrderResponse;
import com.backend.security.AuthPrincipal;
import com.backend.security.TokenStore;
import com.backend.service.AuthService;
import com.backend.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Cart -> checkout flow: mycard.html hands off to check.html which calls this. */
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final AuthService authService;
    private final TokenStore tokenStore;

    public OrderController(OrderService orderService, AuthService authService, TokenStore tokenStore) {
        this.orderService = orderService;
        this.authService = authService;
        this.tokenStore = tokenStore;
    }

    @PostMapping
    public OrderResponse checkout(@RequestHeader(value = "Authorization", required = false) String token,
                                   @Valid @RequestBody CheckoutRequest req) {

        AuthPrincipal me = tokenStore.resolve(token);
        Long customerId = (me != null && "customer".equals(me.role)) ? me.id : null;
        return orderService.checkout(customerId, req);
    }

    @GetMapping("/mine")
    public List<OrderResponse> myOrders(@RequestHeader("Authorization") String token) {
        AuthPrincipal me = authService.requireRole(token, "customer");
        return orderService.getCustomerOrders(me.id);
    }
}
