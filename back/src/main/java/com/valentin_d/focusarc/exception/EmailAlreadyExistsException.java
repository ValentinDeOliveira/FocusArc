package com.valentin_d.focusarc.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(final String email) {
        super("Email already exists: " + email);
    }
}
