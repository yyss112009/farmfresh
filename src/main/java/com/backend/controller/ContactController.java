package com.backend.controller;

import com.backend.dto.ContactRequest;
import com.backend.dto.MessageResponse;
import com.backend.service.ContactService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** Public "Contact Us" form submission - contact.html */
@RestController
@RequestMapping("/api/contact")
public class ContactController {

    private final ContactService contactService;

    public ContactController(ContactService contactService) {
        this.contactService = contactService;
    }

    @PostMapping
    public MessageResponse submit(@Valid @RequestBody ContactRequest req) {
        return contactService.submit(req);
    }
}
