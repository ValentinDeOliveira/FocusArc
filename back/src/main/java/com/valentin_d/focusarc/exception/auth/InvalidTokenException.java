package com.valentin_d.focusarc.exception.auth;

import com.valentin_d.focusarc.exception.base.ApiException;
import org.springframework.http.HttpStatus;

public class InvalidTokenException extends ApiException {
    public InvalidTokenException() {
        super("Invalid or expired token", HttpStatus.UNAUTHORIZED);
    }
}