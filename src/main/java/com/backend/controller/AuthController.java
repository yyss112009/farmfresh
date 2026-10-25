package com.backend.controller;

import com.backend.dto.AuthResponse;
import com.backend.dto.LoginRequest;
import com.backend.dto.RegisterCustomerRequest;
import com.backend.dto.RegisterFarmerRequest;
import com.backend.dto.*;
import com.backend.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register/customer")
    public AuthResponse registerCustomer(@Valid @RequestBody RegisterCustomerRequest req) {
        return authService.registerCustomer(req);
    }

    @PostMapping("/register/farmer")
    public AuthResponse registerFarmer(@Valid @RequestBody RegisterFarmerRequest req) {
        return authService.registerFarmer(req);
    }

    @PostMapping("/login")
    public AuthResponse login(@Valid @RequestBody LoginRequest req) {
        return authService.login(req);
    }

    @PostMapping("/logout")
    public void logout(@RequestHeader(value = "Authorization", required = false) String token) {
        authService.logout(token);
    }
}
