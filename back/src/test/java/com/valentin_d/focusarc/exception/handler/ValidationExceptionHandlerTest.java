package com.valentin_d.focusarc.exception.handler;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.constraints.NotBlank;
import org.junit.jupiter.api.Test;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@WebMvcTest(ValidationExceptionHandler.class)
class ValidationExceptionHandlerTest {

    private final ValidationExceptionHandler handler = new ValidationExceptionHandler();

    private static class TestDto {
        @NotBlank
        String name;
    }

    @Test
    void shouldHandleConstraintViolation() {
        // Create invalid DTO
        final var dto = new TestDto();
        dto.name = "";

        // Build and close the ValidatorFactory with try-with-resources to avoid resource leak warnings
        try (final var factory = Validation.buildDefaultValidatorFactory()) {
            final var validator = factory.getValidator();

            Set<ConstraintViolation<TestDto>> violations = validator.validate(dto);
            final var exception = new ConstraintViolationException(violations);

            var response = handler.handleValidationErrors(exception);

            assertEquals(400, response.getStatusCode().value());
            assertNotNull(response.getBody());

            var errors = (List<?>) response.getBody().get("errors");
            assertEquals(1, errors.size());
            var firstError = (Map<?, ?>) errors.get(0);

            assertEquals("name", firstError.get("field"));
            assertEquals("", firstError.get("rejectedValue"));
            // since the message depend on the language platform, we just check it's not null
            assertThat(firstError.get("message")).isNotNull();
        }
    }
}