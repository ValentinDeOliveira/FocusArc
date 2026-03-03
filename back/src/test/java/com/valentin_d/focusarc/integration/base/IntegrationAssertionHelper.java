package com.valentin_d.focusarc.integration.base;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static com.mongodb.assertions.Assertions.assertNotNull;
import static com.mongodb.assertions.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

public final class IntegrationAssertionHelper {

    public <T> void assertNotFound(final ResponseEntity<T> response) {
        assertResponseEmpty(HttpStatus.NOT_FOUND, response);
    }

    public <T> void assertBadRequest(final ResponseEntity<T> response) {
        assertResponseEmpty(HttpStatus.BAD_REQUEST, response);
    }

    public <T> void assertNoContent(final ResponseEntity<T> response) {
        assertResponseEmpty(HttpStatus.NO_CONTENT, response);
    }

    public <T> void assertOk(final ResponseEntity<T> response) {
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    private  <T> void assertResponseEmpty(final HttpStatus status,final ResponseEntity<T> response) {
        assertEquals(status, response.getStatusCode());
        assertNull(response.getBody());
    }

    public  <T> void assertCreated(final ResponseEntity<T> response) {
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
    }

    public <T> T expectedValue(T newValue, T originalValue) {
        return newValue != null ? newValue : originalValue;
    }
}