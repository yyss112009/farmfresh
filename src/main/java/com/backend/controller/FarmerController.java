package com.backend.controller;

import com.backend.dto.*;
import com.backend.security.AuthPrincipal;
import com.backend.service.AuthService;
import com.backend.service.FarmerProfileService;
import com.backend.service.NotificationService;
import com.backend.service.OrderService;
import com.backend.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/** Everything behind the farmer dashboard: add12.html, manageproduct.html, orders.html, profile.html */
@RestController
@RequestMapping("/api/farmer")
public class FarmerController {

    private final AuthService authService;
    private final ProductService productService;
    private final OrderService orderService;
    private final FarmerProfileService farmerProfileService;
    private final NotificationService notificationService;

    public FarmerController(AuthService authService,
                            ProductService productService,
                            OrderService orderService,
                            FarmerProfileService farmerProfileService,
                            NotificationService notificationService) {
        this.authService = authService;
        this.productService = productService;
        this.orderService = orderService;
        this.farmerProfileService = farmerProfileService;
        this.notificationService = notificationService;
    }

    @GetMapping("/products")
    public List<ProductResponse> myProducts(@RequestHeader("Authorization") String token) {
        AuthPrincipal me = authService.requireRole(token, "farmer");
        return productService.getFarmerProducts(me.id);
    }

    @PostMapping("/products")
    public ProductResponse addProduct(@RequestHeader("Authorization") String token,
                                       @Valid @RequestBody ProductRequest req) {
        AuthPrincipal me = authService.requireRole(token, "farmer");
        return productService.addProduct(me.id, req);
    }

    @PutMapping("/products/{id}")
    public ProductResponse updateProduct(@RequestHeader("Authorization") String token,
                                          @PathVariable Long id, @Valid @RequestBody ProductRequest req) {
        AuthPrincipal me = authService.requireRole(token, "farmer");
        return productService.updateProduct(me.id, id, req);
    }

    @DeleteMapping("/products/{id}")
    public void deleteProduct(@RequestHeader("Authorization") String token, @PathVariable Long id) {
        AuthPrincipal me = authService.requireRole(token, "farmer");
        productService.deleteProduct(me.id, id);
    }

    @GetMapping("/orders")
    public List<OrderItemResponse> myOrders(@RequestHeader("Authorization") String token) {
        AuthPrincipal me = authService.requireRole(token, "farmer");
        return orderService.getFarmerOrderItems(me.id);
    }

    @PutMapping("/orders/{orderId}/status")
    public void updateOrderStatus(@RequestHeader("Authorization") String token,
                                   @PathVariable Long orderId, @Valid @RequestBody StatusUpdateRequest req) {
        AuthPrincipal me = authService.requireRole(token, "farmer");
        orderService.updateStatusAsFarmer(me.id, orderId, req.status);
    }

    @GetMapping("/profile")
    public FarmerResponse getProfile(@RequestHeader("Authorization") String token) {
        AuthPrincipal me = authService.requireRole(token, "farmer");
        return farmerProfileService.getProfile(me.id);
    }

    @PutMapping("/profile")
    public FarmerResponse updateProfile(@RequestHeader("Authorization") String token,
                                         @RequestBody FarmerProfileRequest req) {
        AuthPrincipal me = authService.requireRole(token, "farmer");
        return farmerProfileService.updateProfile(me.id, req);
    }

    /* ---- Farmer Notifications ---- */

    @GetMapping("/notifications")
    public List<NotificationResponse> getNotifications(@RequestHeader("Authorization") String token) {
        AuthPrincipal me = authService.requireRole(token, "farmer");
        return notificationService.getFarmerNotifications(me.id);
    }

    @GetMapping("/notifications/unread-count")
    public Map<String, Long> getUnreadNotificationCount(@RequestHeader("Authorization") String token) {
        AuthPrincipal me = authService.requireRole(token, "farmer");
        long count = notificationService.getUnreadCount(me.id);
        return Collections.singletonMap("unreadCount", count);
    }

    @PutMapping("/notifications/{id}/read")
    public Map<String, Boolean> markNotificationAsRead(@RequestHeader("Authorization") String token,
                                                       @PathVariable Long id) {
        AuthPrincipal me = authService.requireRole(token, "farmer");
        notificationService.markAsRead(me.id, id);
        return Collections.singletonMap("success", true);
    }

    @PutMapping("/notifications/mark-all-read")
    public Map<String, Boolean> markAllNotificationsAsRead(@RequestHeader("Authorization") String token) {
        AuthPrincipal me = authService.requireRole(token, "farmer");
        notificationService.markAllAsRead(me.id);
        return Collections.singletonMap("success", true);
    }
}
