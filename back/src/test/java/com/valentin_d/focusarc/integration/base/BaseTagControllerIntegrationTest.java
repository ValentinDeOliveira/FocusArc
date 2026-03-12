package com.valentin_d.focusarc.integration.base;

import com.valentin_d.focusarc.model.id.TagId;
import com.valentin_d.focusarc.model.tag.Tag;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BaseTagControllerIntegrationTest extends SecuredIntegrationTest {
    protected final String URL = "/tags";

    protected void assertTagEquals(final Tag expected, final Tag actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getOwner(), actual.getOwner());
        assertEquals(expected.getLabel(), actual.getLabel());
        assertEquals(expected.getColor(), actual.getColor());
    }

    protected String tagUrl(final TagId tagId) {
        return URL + "/" + tagId.id();
    }
}
