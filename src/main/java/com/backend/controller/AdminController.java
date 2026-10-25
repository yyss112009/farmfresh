package com.backend.controller;

import com.backend.dto.*;
import com.backend.dto.*;
import com.backend.dto.*;
import com.backend.service.AdminService;
import com.backend.service.AuthService;
import com.backend.service.ContactService;
import com.backend.service.OrderService;
import com.backend.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** Everything behind the admin dashboard: dash.html, farmer.html, customer.html, product.html, order.html, msg.html */
@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AuthService authService;
    private final AdminService adminService;
    private final ProductService productService;
    private final ContactService contactService;
    private final OrderService orderService;

    public AdminController(AuthService authService, AdminService adminService,
                            ProductService productService, ContactService contactService,
                            OrderService orderService) {
        this.authService = authService;
        this.adminService = adminService;
        this.productService = productService;
        this.contactService = contactService;
        this.orderService = orderService;
    }


    @GetMapping("/dashboard")
    public DashboardStats dashboard(@RequestHeader("Authorization") String token) {
        authService.requireRole(token, "admin");
        return adminService.getStats();
    }


    @GetMapping("/farmers")
    public List<AdminFarmerRow> farmers(@RequestHeader("Authorization") String token) {
        authService.requireRole(token, "admin");
        return adminService.getFarmerRows();
    }

    @PutMapping("/farmers/{id}/status")
    public FarmerResponse updateFarmerStatus(@RequestHeader("Authorization") String token,
                                             @PathVariable Long id, @Valid @RequestBody StatusUpdateRequest req) {
        authService.requireRole(token, "admin");
        return adminService.updateFarmerStatus(id, req.status);
    }


    @GetMapping("/customers")
    public List<AdminCustomerRow> customers(@RequestHeader("Authorization") String token) {
        authService.requireRole(token, "admin");
        return adminService.getCustomerRows();
    }

    @PutMapping("/customers/{id}/status")
    public void updateCustomerStatus(@RequestHeader("Authorization") String token,
                                      @PathVariable Long id, @Valid @RequestBody StatusUpdateRequest req) {
        authService.requireRole(token, "admin");
        adminService.updateCustomerStatus(id, req.status);
    }

    @GetMapping("/products")
    public List<ProductResponse> products(@RequestHeader("Authorization") String token) {
        authService.requireRole(token, "admin");
        return productService.getAllProductsForAdmin();
    }

    @PutMapping("/products/{id}/status")
    public ProductResponse updateProductStatus(@RequestHeader("Authorization") String token,
                                                @PathVariable Long id, @Valid @RequestBody StatusUpdateRequest req) {
        authService.requireRole(token, "admin");
        return productService.updateStatusAsAdmin(id, req.status);
    }

    @GetMapping("/orders")
    public List<AdminOrderRow> orders(@RequestHeader("Authorization") String token) {
        authService.requireRole(token, "admin");
        return adminService.getOrderRows();
    }

    @PutMapping("/orders/{id}/status")
    public OrderResponse updateOrderStatus(@RequestHeader("Authorization") String token,
                                           @PathVariable Long id, @Valid @RequestBody StatusUpdateRequest req) {
        authService.requireRole(token, "admin");
        return orderService.updateStatus(id, req.status);
    }


    @GetMapping("/messages")
    public List<MessageResponse> messages(@RequestHeader("Authorization") String token) {
        authService.requireRole(token, "admin");
        return contactService.getAll();
    }

    @PutMapping("/messages/{id}/status")
    public MessageResponse updateMessageStatus(@RequestHeader("Authorization") String token,
                                                @PathVariable Long id, @Valid @RequestBody StatusUpdateRequest req) {
        authService.requireRole(token, "admin");
        return contactService.updateStatus(id, req.status);
    }
}
