package com.backend.exception;

import org.springframework.http.HttpStatus;

/** Thrown anywhere in service/controller code to return a clean JSON error instead of a stack trace. */
public class ApiException extends RuntimeException {
    private final HttpStatus status;

    public ApiException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() { return status; }
}
