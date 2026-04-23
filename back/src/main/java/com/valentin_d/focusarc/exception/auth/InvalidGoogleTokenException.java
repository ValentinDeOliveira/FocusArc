package com.valentin_d.focusarc.exception.auth;

import com.valentin_d.focusarc.exception.base.ApiException;
import org.springframework.http.HttpStatus;

public class InvalidGoogleTokenException extends ApiException {
    public InvalidGoogleTokenException() {
        super("Invalid or expired Google ID token", HttpStatus.UNAUTHORIZED);
    }
}