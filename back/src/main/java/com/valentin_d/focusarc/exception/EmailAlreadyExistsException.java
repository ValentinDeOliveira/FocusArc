package com.valentin_d.focusarc.exception;

import org.springframework.http.HttpStatus;

public class EmailAlreadyExistsException extends ApiException {
    public EmailAlreadyExistsException(final String email) {
        super("Email already exists: " + email, HttpStatus.CONFLICT);
    }
}