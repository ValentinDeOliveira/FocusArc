package com.valentin_d.focusarc.integration.base;

import com.valentin_d.focusarc.model.user.User;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BaseUserControllerIntegrationTest extends SecuredIntegrationTest {
    protected final String URL = "/users";

    protected void assertGetUserEquals(final User expected, final User actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getEmail(), actual.getEmail());
        assertEquals(expected.getLastLogin(), actual.getLastLogin());
        assertEquals(expected.getAuthProvider(), actual.getAuthProvider());
    }
}