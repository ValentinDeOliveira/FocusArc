package com.valentin_d.focusarc.exception.handler;

import com.valentin_d.focusarc.exception.base.ApiException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, Object>> handleApiErrors(ApiException ex) {
        final var response = Map.<String, Object>of(
                "timestamp", Instant.now(),
                "status", ex.getStatus().value(),
                "error", ex.getClass().getSimpleName(),
                "message", ex.getMessage()
        );
        return ResponseEntity.status(ex.getStatus()).body(response);
    }
}