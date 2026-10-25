package com.backend.dto;

/** Sent back after successful login/register. Frontend stores this in localStorage. */
public class AuthResponse {
    public String token;
    public String role;
    public Long id;
    public String fullName;
    public String email;

    public AuthResponse(String token, String role, Long id, String fullName, String email) {
        this.token = token;
        this.role = role;
        this.id = id;
        this.fullName = fullName;
        this.email = email;
    }
}
