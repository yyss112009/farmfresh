package com.backend.dto;

import jakarta.validation.constraints.NotBlank;

public class ContactRequest {
    @NotBlank public String name;
    @NotBlank public String email;
    public String subject;
    @NotBlank public String message;
}
