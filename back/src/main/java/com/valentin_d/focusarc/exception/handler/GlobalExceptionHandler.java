package com.valentin_d.focusarc.exception.handler;

import com.valentin_d.focusarc.exception.base.ApiException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ApiException.class)
    public ResponseEntity<Map<String, Object>> handleApiErrors(ApiException ex) {
        var response = new HashMap<String, Object>();

        response.put("timestamp", Instant.now());
        response.put("status", ex.getStatus().value());
        response.put("error", ex.getClass().getSimpleName());
        response.put("message", ex.getMessage());

        if (ex.getDetails() != null) {
            response.put("details", ex.getDetails());
        }

        return ResponseEntity.status(ex.getStatus()).body(response);
    }
}