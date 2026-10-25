package com.backend.security;

/** Who is making the request, resolved from the Authorization token. */
public class AuthPrincipal {
    public final Long id;
    public final String role; // customer / farmer / admin
    public final String email;
    public final String fullName;

    public AuthPrincipal(Long id, String role, String email, String fullName) {
        this.id = id;
        this.role = role;
        this.email = email;
        this.fullName = fullName;
    }
}
