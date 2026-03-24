package com.valentin_d.focusarc.repository;

import com.valentin_d.focusarc.model.id.ChapterId;
import com.valentin_d.focusarc.model.id.TaskId;
import com.valentin_d.focusarc.model.task.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.mongodb.test.autoconfigure.DataMongoTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static com.valentin_d.focusarc.fixtures.factory.TaskFactory.aTaskWithChapterId;
import static com.valentin_d.focusarc.fixtures.factory.TaskFactory.aTaskWithChapterIdAndWindow;
import static com.valentin_d.focusarc.model.task.TaskStatus.PENDING;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCollection;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DataMongoTest
@ActiveProfiles("test")
class TaskRepositoryTest {
    @Autowired
    private TaskRepository repository;

    // New task window: [10:00, 10:30]
    private static final Instant NEW_START = Instant.parse("2025-01-01T10:00:00Z");
    private static final Instant NEW_END   = NEW_START.plus(30, ChronoUnit.MINUTES);

    @BeforeEach
    void setUp() {
        repository.deleteAll();
    }

    @Test
    void shouldReturnAllChapter_whenArcExists() {
        final var chapterId = ChapterId.random();

        final var task1 = aTaskWithChapterId(chapterId);
        final var task2 = aTaskWithChapterId(chapterId);
        repository.save(task1);
        repository.save(task2);

        final var arcsLists = repository.findAllByChapterOrderByStartAtAsc(chapterId);
        assertEquals(2, arcsLists.size());
        assertThatCollection(arcsLists).containsExactly(task1, task2);
    }

    // existsByChapterAndStatusInAndStartAtBeforeAndEndAtAfter
    @Test
    void shouldDetectOverlap_whenExistingTaskStartsBeforeAndEndsInside() {
        // existing [09:30, 10:15], starts before new, ends inside new
        final var chapterId = ChapterId.random();
        repository.save(aTaskWithChapterIdAndWindow(chapterId, NEW_START.minus(30, ChronoUnit.MINUTES), 45));

        assertThat(existsOverlap(chapterId)).isTrue();
    }

    @Test
    void shouldDetectOverlap_whenExistingTaskStartsInsideAndEndsAfter() {
        // existing [10:15, 10:45], starts inside new, ends after new
        final var chapterId = ChapterId.random();
        repository.save(aTaskWithChapterIdAndWindow(chapterId, NEW_START.plus(15, ChronoUnit.MINUTES), 30));

        assertThat(existsOverlap(chapterId)).isTrue();
    }

    @Test
    void shouldDetectOverlap_whenExistingTaskWrapsAround() {
        // existing [09:30, 10:45], starts before new, ends after new
        final var chapterId = ChapterId.random();
        repository.save(aTaskWithChapterIdAndWindow(chapterId, NEW_START.minus(30, ChronoUnit.MINUTES), 75));

        assertThat(existsOverlap(chapterId)).isTrue();
    }

    @Test
    void shouldDetectOverlap_whenExistingTaskIsContainedInside() {
        // existing [10:05, 10:25], fully contained inside new
        final var chapterId = ChapterId.random();
        repository.save(aTaskWithChapterIdAndWindow(chapterId, NEW_START.plus(5, ChronoUnit.MINUTES), 20));

        assertThat(existsOverlap(chapterId)).isTrue();
    }

    @Test
    void shouldNotDetectOverlap_whenExistingTaskEndsBefore() {
        // existing [08:00, 09:30], ends well before new starts
        final var chapterId = ChapterId.random();
        repository.save(aTaskWithChapterIdAndWindow(chapterId, NEW_START.minus(120, ChronoUnit.MINUTES), 90));

        assertThat(existsOverlap(chapterId)).isFalse();
    }

    @Test
    void shouldNotDetectOverlap_whenExistingTaskStartsAfter() {
        // existing [11:00, 11:30], starts well after new ends
        final var chapterId = ChapterId.random();
        repository.save(aTaskWithChapterIdAndWindow(chapterId, NEW_END.plus(30, ChronoUnit.MINUTES), 30));

        assertThat(existsOverlap(chapterId)).isFalse();
    }

    @Test
    void shouldNotDetectOverlap_whenExistingTaskIsAdjacentBefore() {
        // existing [09:30, 10:00], ends exactly at NEW_START (touching, not overlapping)
        final var chapterId = ChapterId.random();
        repository.save(aTaskWithChapterIdAndWindow(chapterId, NEW_START.minus(30, ChronoUnit.MINUTES), 30));

        assertThat(existsOverlap(chapterId)).isFalse();
    }

    @Test
    void shouldNotDetectOverlap_whenExistingTaskIsAdjacentAfter() {
        // existing [10:30, 11:00], starts exactly at NEW_END (touching, not overlapping)
        final var chapterId = ChapterId.random();
        repository.save(aTaskWithChapterIdAndWindow(chapterId, NEW_END, 30));

        assertThat(existsOverlap(chapterId)).isFalse();
    }

    @Test
    void shouldNotDetectOverlap_whenExistingOverlappingTaskIsDone() {
        // overlapping window but DONE, should be ignored
        final var chapterId = ChapterId.random();
        final var task = aTaskWithChapterIdAndWindow(chapterId, NEW_START.plus(15, ChronoUnit.MINUTES), 30);
        task.setStatus(TaskStatus.DONE);
        repository.save(task);

        assertThat(existsOverlap(chapterId)).isFalse();
    }

    @Test
    void shouldNotDetectOverlap_whenExistingOverlappingTaskIsSkipped() {
        // overlapping window but SKIPPED, should be ignored
        final var chapterId = ChapterId.random();
        final var task = aTaskWithChapterIdAndWindow(chapterId, NEW_START.plus(15, ChronoUnit.MINUTES), 30);
        task.setStatus(TaskStatus.SKIPPED);
        repository.save(task);

        assertThat(existsOverlap(chapterId)).isFalse();
    }

    @Test
    void shouldNotDetectOverlap_whenOverlappingTaskIsInDifferentChapter() {
        // same window, but different chapterId
        final var chapterId = ChapterId.random();
        final var otherChapterId = ChapterId.random();
        repository.save(aTaskWithChapterIdAndWindow(otherChapterId, NEW_START.plus(15, ChronoUnit.MINUTES), 30));

        assertThat(existsOverlap(chapterId)).isFalse();
    }

    private boolean existsOverlap(final ChapterId chapterId) {
        return repository.existsByChapterAndStatusInAndStartAtBeforeAndEndAtAfter(
                chapterId, PENDING, NEW_END, NEW_START);
    }

    // -------------------------------------------------------------------------
    // existsByChapterAndStatusInAndIdNotAndStartAtBeforeAndEndAtAfter
    // Same overlap logic, but excludes a specific taskId (used on update)
    // -------------------------------------------------------------------------

    @Test
    void shouldNotDetectOverlap_whenOverlappingTaskIsTheExcludedTask() {
        final var chapterId = ChapterId.random();
        final var task = aTaskWithChapterIdAndWindow(chapterId, NEW_START.plus(15, ChronoUnit.MINUTES), 30);
        repository.save(task);

        assertThat(existsOverlapExcluding(chapterId, task.getId())).isFalse();
    }

    @Test
    void shouldDetectOverlap_whenOverlappingTaskIsNotTheExcludedTask() {
        final var chapterId = ChapterId.random();
        final var existingTask = aTaskWithChapterIdAndWindow(chapterId, NEW_START.plus(15, ChronoUnit.MINUTES), 30);
        final var otherTask = aTaskWithChapterIdAndWindow(chapterId, NEW_START.plus(10, ChronoUnit.MINUTES), 30);
        repository.save(existingTask);
        repository.save(otherTask);

        assertThat(existsOverlapExcluding(chapterId, existingTask.getId())).isTrue();
    }

    private boolean existsOverlapExcluding(final ChapterId chapterId, final TaskId excludedId) {
        return repository.existsByChapterAndStatusInAndIdNotAndStartAtBeforeAndEndAtAfter(
                chapterId, PENDING, excludedId, NEW_END, NEW_START);
    }
}