package com.valentin_d.focusarc.exception.base;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.Map;

public abstract class ApiException extends RuntimeException {
    @Getter
    private final HttpStatus status;
    @Getter
    private Map<String, Object> details;

    public ApiException(final String message) {
        super(message);
        this.status = HttpStatus.BAD_REQUEST;
    }

    public ApiException(final String message, final HttpStatus status) {
        super(message);
        this.status = status;
    }

    public ApiException(final String message, final HttpStatus status, final Map<String, Object> details) {
        super(message);
        this.status = status;
        this.details = details;
    }
}