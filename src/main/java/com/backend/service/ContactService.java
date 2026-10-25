package com.backend.service;

import com.backend.dto.ContactRequest;
import com.backend.dto.MessageResponse;
import com.backend.entity.ContactMessage;
import com.backend.exception.ApiException;
import com.backend.repository.ContactMessageRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContactService {

    private final ContactMessageRepository repository;

    public ContactService(ContactMessageRepository repository) {
        this.repository = repository;
    }

    public MessageResponse submit(ContactRequest req) {
        ContactMessage m = new ContactMessage();
        m.setName(req.name);
        m.setEmail(req.email);
        m.setSubject(req.subject);
        m.setMessage(req.message);
        repository.save(m);
        return MessageResponse.from(m);
    }

    public List<MessageResponse> getAll() {
        return repository.findAll().stream().map(MessageResponse::from).collect(Collectors.toList());
    }

    public MessageResponse updateStatus(Long id, String status) {
        ContactMessage m = repository.findById(id)
                .orElseThrow(() -> new ApiException("Message not found.", HttpStatus.NOT_FOUND));
        m.setStatus(status);
        repository.save(m);
        return MessageResponse.from(m);
    }
}
