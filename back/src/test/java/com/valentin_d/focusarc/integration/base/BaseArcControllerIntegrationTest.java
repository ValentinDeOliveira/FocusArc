package com.valentin_d.focusarc.integration.base;

import com.valentin_d.focusarc.model.arc.Arc;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BaseArcControllerIntegrationTest extends SecuredIntegrationTest {
    protected final String URL = "/arcs";

    protected void assertArcEquals(final Arc expected, final Arc actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getName(), actual.getName());
        assertEquals(expected.getOwner(), actual.getOwner());
        assertEquals(expected.getTotalEstimatedMinutes(), actual.getTotalEstimatedMinutes());
        assertEquals(expected.getTotalCompletedMinutes(), actual.getTotalCompletedMinutes());
    }
}