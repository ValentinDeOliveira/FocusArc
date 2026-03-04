package com.valentin_d.focusarc.exception;

import org.springframework.http.HttpStatus;

public class InvalidTokenException extends ApiException {
    public InvalidTokenException() {
        super("Invalid or expired token", HttpStatus.UNAUTHORIZED);
    }
}
