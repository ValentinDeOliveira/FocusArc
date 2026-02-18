package com.valentin_d.focusarc.exception.handler;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class ValidationExceptionHandler {

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, Object>> handleValidationErrors(ConstraintViolationException ex) {
        Map<String, Object> response = new HashMap<>();
        List<Map<String, String>> errors = ex.getConstraintViolations()
                .stream()
                .map(v -> Map.of(
                        "field", v.getPropertyPath().toString(),
                        "rejectedValue", v.getInvalidValue() == null ? "null" : v.getInvalidValue().toString(),
                        "message", v.getMessage()
                ))
                .toList();

        response.put("timestamp", Instant.now());
        response.put("status", 400);
        response.put("errors", errors);
        return ResponseEntity.badRequest().body(response);
    }
}