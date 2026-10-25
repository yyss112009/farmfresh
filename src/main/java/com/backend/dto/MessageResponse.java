package com.backend.dto;

import com.backend.entity.ContactMessage;
import java.time.LocalDateTime;

public class MessageResponse {
    public Long id;
    public String name;
    public String email;
    public String subject;
    public String message;
    public String status;
    public LocalDateTime createdAt;

    public static MessageResponse from(ContactMessage m) {
        MessageResponse r = new MessageResponse();
        r.id = m.getId();
        r.name = m.getName();
        r.email = m.getEmail();
        r.subject = m.getSubject();
        r.message = m.getMessage();
        r.status = m.getStatus();
        r.createdAt = m.getCreatedAt();
        return r;
    }
}
