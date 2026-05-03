package com.valentin_d.focusarc.integration.base;

import com.valentin_d.focusarc.model.arc.Arc;
import com.valentin_d.focusarc.model.id.ArcId;
import com.valentin_d.focusarc.model.id.ChapterId;

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

    protected String arcUrl(final ArcId arcId) {
        return URL + "/" + arcId.id();
    }

    protected String massCreateUrl(final ArcId arcId) {
        return URL + "/" + arcId.id() + "/tasks/init";
    }

    protected String chaptersForArcUrl(final ArcId arcId) {
        return "/chapters/arcs/" + arcId.id();
    }

    protected String tasksForChapterUrl(final ChapterId chapterId) {
        return "/tasks/chapters/" + chapterId.id();
    }
}