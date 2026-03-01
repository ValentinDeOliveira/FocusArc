package com.valentin_d.focusarc.integration.base;

import com.valentin_d.focusarc.model.Chapter;
import com.valentin_d.focusarc.model.arc.Arc;
import com.valentin_d.focusarc.model.id.ArcId;
import com.valentin_d.focusarc.model.id.ChapterId;
import com.valentin_d.focusarc.model.task.Task;
import com.valentin_d.focusarc.model.task.TaskStatus;
import com.valentin_d.focusarc.repository.ArcRepository;
import com.valentin_d.focusarc.repository.ChapterRepository;
import com.valentin_d.focusarc.repository.TaskRepository;
import com.valentin_d.focusarc.service.ContextLoader;
import com.valentin_d.focusarc.service.chapter.ChapterLoader;
import com.valentin_d.focusarc.service.task.TaskLoader;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;

import static com.valentin_d.focusarc.fixtures.factory.ArcFactory.anArc;
import static com.valentin_d.focusarc.fixtures.factory.ChapterFactory.aChapterWithArcId;
import static com.valentin_d.focusarc.fixtures.factory.ChapterFactory.aChapterWithScheduledDateAndArcId;
import static com.valentin_d.focusarc.fixtures.factory.TaskFactory.aTaskWithChapterIdAndStatus;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BaseChapterControllerIntegrationTest extends BaseIntegrationTest{
    @Autowired
    protected ChapterRepository chapterRepository;
    @Autowired
    protected ArcRepository arcRepository;
    @Autowired
    protected ChapterLoader chapterLoader;
    @Autowired
    protected TaskLoader taskLoader;
    @Autowired
    protected TaskRepository taskRepository;
    @Autowired
    protected ContextLoader contextLoader;
    protected final String URL = "/chapters";

    @BeforeEach
    public void setUp() {
        arcRepository.deleteAll();
        chapterRepository.deleteAll();
    }

    protected void assertChaptersEquals(final Chapter expected, final Chapter actual) {
        assertEquals(expected.getId(), actual.getId());
        assertEquals(expected.getCompletedMinutes(), actual.getCompletedMinutes());
        assertEquals(expected.getEstimatedMinutes(), actual.getEstimatedMinutes());
        assertEquals(expected.getArc(), actual.getArc());
        assertEquals(expected.getScheduledDate(), actual.getScheduledDate());
    }

    protected Arc createArc() {
        final var arc = anArc();
        return arcRepository.save(arc);
    }

    protected Chapter createChapter() {
        final var arc = createArc();
        return createChapterForArc(arc.getId());
    }

    protected Chapter createChapterForArc(final ArcId arcId) {
        final var chapter = aChapterWithArcId(arcId);
        return chapterRepository.save(chapter);
    }

    protected Chapter createChapterForArcWithDate(final ArcId arcId, final LocalDate date) {
        final var chapter = aChapterWithScheduledDateAndArcId(date, arcId);
        return chapterRepository.save(chapter);
    }

    protected Task createTaskForChapterWithStatus(final ChapterId chapterId, TaskStatus status) {
        final var task = aTaskWithChapterIdAndStatus(chapterId, status);
        return taskRepository.save(task);
    }
}