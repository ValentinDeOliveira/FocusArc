package com.valentin_d.focusarc.exception.handler;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@WebMvcTest(ValidationExceptionHandler.class)
class ValidationExceptionHandlerTest {

    private final ValidationExceptionHandler handler = new ValidationExceptionHandler();

    private static class TestDto {
        @NotBlank
        String name;
        @NotNull
        String notNullField;
    }

    @Test
    void shouldHandleConstraintViolationOnEmpty() {
        // Create invalid DTO
        final var dto = new TestDto();
        dto.name = "";
        dto.notNullField = "foobar";

        final var response = validateAndHandle(dto);
        assertSingleError(response, "name", "");
    }

    @Test
    void shouldHandleConstraintViolationOnEmail() {
        // Create invalid DTO
        final var dto = new TestDto();
        dto.name = "foobar";
        dto.notNullField = null;

        final var response = validateAndHandle(dto);
        assertSingleError(response, "notNullField", "null");
    }

    private ResponseEntity<Map<String, Object>> validateAndHandle(final TestDto dto) {
        try (final var factory = Validation.buildDefaultValidatorFactory()) {
            final var validator = factory.getValidator();

            final var violations = validator.validate(dto);
            final var exception = new ConstraintViolationException(violations);

            return handler.handleValidationErrors(exception);
        }
    }

    private void assertSingleError(final ResponseEntity<Map<String, Object>> response,
                                   final String expectedField,
                                   final String expectedRejectedValue) {
        assertEquals(400, response.getStatusCode().value());
        assertNotNull(response.getBody());

        final var errors = (List<?>) response.getBody().get("errors");
        assertEquals(1, errors.size());
        final var firstError = (Map<?, ?>) errors.get(0);

        assertEquals(expectedField, firstError.get("field"));
        assertEquals(expectedRejectedValue, firstError.get("rejectedValue"));
        // since the message depend on the language platform, we just check it's not null
        assertThat(firstError.get("message")).isNotNull();
    }
}