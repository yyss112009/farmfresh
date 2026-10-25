package com.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
    @NotBlank private String email;
    @NotBlank private String password;
    @NotBlank private String role; // customer / farmer / admin

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
