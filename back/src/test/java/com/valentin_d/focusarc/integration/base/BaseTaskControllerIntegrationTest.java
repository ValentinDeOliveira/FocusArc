package com.valentin_d.focusarc.integration.base;

import com.valentin_d.focusarc.model.task.Task;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BaseTaskControllerIntegrationTest extends SecuredIntegrationTest {
    protected final String URL = "/tasks";

    protected void assertTasksEquals(final Task expected, final Task actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getCompletedMinutes(), actual.getCompletedMinutes());
        assertEquals(expected.getEstimatedMinutes(), actual.getEstimatedMinutes());
        assertEquals(expected.getChapter(), actual.getChapter());
        assertEquals(expected.getStatus(), actual.getStatus());
        assertEquals(expected.getScheduledAt(), actual.getScheduledAt());
    }

    protected <T> ResponseEntity<T> exchangeTodayForUser(Class<T> responseType) {
        return request(URL + "/today", HttpMethod.GET, getHttpEntity(), responseType);
    }
}