package com.backend.service;

import com.backend.dto.AuthResponse;
import com.backend.dto.LoginRequest;
import com.backend.dto.RegisterCustomerRequest;
import com.backend.dto.RegisterFarmerRequest;
import com.backend.dto.*;
import com.backend.entity.Admin;
import com.backend.entity.Customer;
import com.backend.entity.Farmer;
import com.backend.exception.ApiException;
import com.backend.repository.AdminRepository;
import com.backend.repository.CustomerRepository;
import com.backend.repository.FarmerRepository;
import com.backend.security.AuthPrincipal;
import com.backend.security.TokenStore;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final CustomerRepository customerRepository;
    private final FarmerRepository farmerRepository;
    private final AdminRepository adminRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenStore tokenStore;

    public AuthService(CustomerRepository customerRepository, FarmerRepository farmerRepository,
                        AdminRepository adminRepository, PasswordEncoder passwordEncoder, TokenStore tokenStore) {
        this.customerRepository = customerRepository;
        this.farmerRepository = farmerRepository;
        this.adminRepository = adminRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenStore = tokenStore;
    }

    public AuthResponse registerCustomer(RegisterCustomerRequest req) {
        if (customerRepository.existsByEmail(req.getEmail())) {
            throw new ApiException("An account with this email already exists.", HttpStatus.CONFLICT);
        }
        Customer c = new Customer();
        c.setFullName(req.getFullName());
        c.setEmail(req.getEmail());
        c.setMobile(req.getMobile());
        c.setState(req.getState());
        c.setPassword(passwordEncoder.encode(req.getPassword()));
        customerRepository.save(c);

        String token = tokenStore.issueToken(new AuthPrincipal(c.getId(), "customer", c.getEmail(), c.getFullName()));
        return new AuthResponse(token, "customer", c.getId(), c.getFullName(), c.getEmail());
    }

    public AuthResponse registerFarmer(RegisterFarmerRequest req) {
        if (farmerRepository.existsByEmail(req.getEmail())) {
            throw new ApiException("An account with this email already exists.", HttpStatus.CONFLICT);
        }
        Farmer f = new Farmer();
        f.setFullName(req.getFullName());
        f.setEmail(req.getEmail());
        f.setMobile(req.getMobile());
        f.setFarmName(req.getFarmName());
        f.setFarmAddress(req.getFarmAddress());
        f.setState(req.getState());
        f.setPassword(passwordEncoder.encode(req.getPassword()));
        farmerRepository.save(f);

        String token = tokenStore.issueToken(new AuthPrincipal(f.getId(), "farmer", f.getEmail(), f.getFullName()));
        return new AuthResponse(token, "farmer", f.getId(), f.getFullName(), f.getEmail());
    }

    public AuthResponse login(LoginRequest req) {
        String role = req.getRole() == null ? "" : req.getRole().toLowerCase();

        switch (role) {
            case "customer": {
                Customer c = customerRepository.findByEmail(req.getEmail())
                        .orElseThrow(() -> new ApiException("Invalid email or password.", HttpStatus.UNAUTHORIZED));
                checkPassword(req.getPassword(), c.getPassword());
                if ("BLOCKED".equalsIgnoreCase(c.getStatus())) {
                    throw new ApiException("Your account has been blocked. Contact support.", HttpStatus.FORBIDDEN);
                }
                String token = tokenStore.issueToken(new AuthPrincipal(c.getId(), "customer", c.getEmail(), c.getFullName()));
                return new AuthResponse(token, "customer", c.getId(), c.getFullName(), c.getEmail());
            }
            case "farmer": {
                Farmer f = farmerRepository.findByEmail(req.getEmail())
                        .orElseThrow(() -> new ApiException("Invalid email or password.", HttpStatus.UNAUTHORIZED));
                checkPassword(req.getPassword(), f.getPassword());
                if ("blocked".equalsIgnoreCase(f.getStatus())) {
                    throw new ApiException("Your farmer account has been blocked by admin.", HttpStatus.FORBIDDEN);
                }
                String token = tokenStore.issueToken(new AuthPrincipal(f.getId(), "farmer", f.getEmail(), f.getFullName()));
                return new AuthResponse(token, "farmer", f.getId(), f.getFullName(), f.getEmail());
            }
            case "admin": {
                Admin a = adminRepository.findByEmail(req.getEmail())
                        .orElseThrow(() -> new ApiException("Invalid email or password.", HttpStatus.UNAUTHORIZED));
                checkPassword(req.getPassword(), a.getPassword());
                String token = tokenStore.issueToken(new AuthPrincipal(a.getId(), "admin", a.getEmail(), a.getFullName()));
                return new AuthResponse(token, "admin", a.getId(), a.getFullName(), a.getEmail());
            }
            default:
                throw new ApiException("Please select a valid user type.", HttpStatus.BAD_REQUEST);
        }
    }

    private void checkPassword(String raw, String hashed) {
        if (!passwordEncoder.matches(raw, hashed)) {
            throw new ApiException("Invalid email or password.", HttpStatus.UNAUTHORIZED);
        }
    }

    public AuthPrincipal requireAuth(String authHeader) {
        AuthPrincipal p = tokenStore.resolve(authHeader);
        if (p == null) {
            throw new ApiException("Please log in again.", HttpStatus.UNAUTHORIZED);
        }
        return p;
    }

    public AuthPrincipal requireRole(String authHeader, String role) {
        AuthPrincipal p = requireAuth(authHeader);
        if (!p.role.equalsIgnoreCase(role)) {
            throw new ApiException("You are not allowed to perform this action.", HttpStatus.FORBIDDEN);
        }
        return p;
    }

    public void logout(String authHeader) {
        tokenStore.revoke(authHeader);
    }
}
