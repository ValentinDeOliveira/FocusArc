package com.valentin_d.focusarc.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

public abstract class ApiException extends RuntimeException {
    @Getter
    private final HttpStatus status;

    public ApiException(final String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }

    public ApiException(final String message, final HttpStatus status) {
        super(message);
        this.status = status;
    }
}