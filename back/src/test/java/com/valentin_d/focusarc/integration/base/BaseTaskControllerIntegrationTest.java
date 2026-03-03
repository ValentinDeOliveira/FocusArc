package com.valentin_d.focusarc.integration.base;

import com.valentin_d.focusarc.model.Chapter;
import com.valentin_d.focusarc.model.id.ChapterId;
import com.valentin_d.focusarc.model.task.Task;
import com.valentin_d.focusarc.model.user.User;
import com.valentin_d.focusarc.repository.ChapterRepository;
import com.valentin_d.focusarc.repository.TaskRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;

import static com.valentin_d.focusarc.fixtures.factory.ChapterFactory.aChapter;
import static com.valentin_d.focusarc.fixtures.factory.TaskFactory.aTaskWithChapterId;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BaseTaskControllerIntegrationTest extends BaseIntegrationTest{
    @Autowired
    protected TaskRepository taskRepository;
    @Autowired
    protected ChapterRepository chapterRepository;
    protected final String URL = "/tasks";

    @BeforeEach
    public void setUp() {
        taskRepository.deleteAll();
        chapterRepository.deleteAll();
    }

    protected void assertTasksEquals(final Task expected, final Task actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getCompletedMinutes(), actual.getCompletedMinutes());
        assertEquals(expected.getEstimatedMinutes(), actual.getEstimatedMinutes());
        assertEquals(expected.getChapter(), actual.getChapter());
        assertEquals(expected.getStatus(), actual.getStatus());
        assertEquals(expected.getScheduledAt(), actual.getScheduledAt());
    }

    protected Chapter createChapter() {
        final var chapter = aChapter();
        return chapterRepository.save(chapter);
    }

    protected Task createTask() {
        final var chapter = createChapter();
        return createTaskForChapter(chapter.getId());
    }

    protected Task createTaskForChapter(final ChapterId arcId) {
        final var task = aTaskWithChapterId(arcId);
        return taskRepository.save(task);
    }

    protected <T> ResponseEntity<T> exchangeTodayForUser(final User user, Class<T> responseType) {
        final var headers = getHeadersForUser(user);

        return request(URL + "/today", HttpMethod.GET, new HttpEntity<>(headers), responseType);
    }
}