package com.valentin_d.focusarc.integration;

import com.valentin_d.focusarc.dto.task.TaskUpdateDto;
import com.valentin_d.focusarc.integration.base.BaseTaskControllerIntegrationTest;
import com.valentin_d.focusarc.model.arc.ArcStatus;
import com.valentin_d.focusarc.model.id.ChapterId;
import com.valentin_d.focusarc.model.id.TaskId;
import com.valentin_d.focusarc.model.task.Task;
import com.valentin_d.focusarc.model.task.TaskStatus;
import com.valentin_d.focusarc.service.chapter.ChapterRecalculationService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static com.valentin_d.focusarc.fixtures.factory.TaskFactory.*;
import static org.assertj.core.api.CollectionAssert.assertThatCollection;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TaskControllerIntegrationTest extends BaseTaskControllerIntegrationTest {
    @MockitoBean
    private ChapterRecalculationService chapterRecalculationService;

    @Test
    void shouldCreateTask_whenDataIsValid() {
        final var chapter = domainFixture.chapterForUser(user.getId());
        final var tag = domainFixture.tagForUser(user.getId());

        final var dto = aTaskCreationDtoWithChapterIdWithTag(chapter.getId(), tag.getId());
        final var response = request(URL, HttpMethod.POST, dto, Task.class);

        assertionHelper.assertCreated(response);

        final var task = response.getBody();
        assertNotNull(task);

        assertEquals(dto.scheduledAt(), task.getStartAt());
        assertEquals(dto.chapterId(), task.getChapter());
        assertEquals(dto.estimatedMinutes(), task.getEstimatedMinutes());
        assertEquals(0, task.getCompletedMinutes());
        assertEquals(TaskStatus.PLANNED, task.getStatus());
        assertEquals(task.getTagId(), dto.tagId());
        assertEquals(task.getName(), dto.name());
        assertEquals(task.getDescription(), dto.description());
        assertNotNull(task.getId());
    }

    @Test
    void shouldReturnNotFoundOnCreate_whenChapterDoesNotExists() {
        final var dto = aTaskCreationDto();

        final var response = request(URL, HttpMethod.POST, dto, Void.class);

        assertionHelper.assertNotFound(response);
    }

    @Test
    void shouldReturnTask_whenIdExists() {
        final var task = domainFixture.taskForUser(user.getId());

        final var response = request(tasksUrl(task.getId()), HttpMethod.GET, Task.class);
        assertionHelper.assertOk(response);

        final var result = response.getBody();
        assertNotNull(result);

        assertTasksEquals(result, task);
    }

    @Test
    void shouldReturnAllTask_whenArcChapterExists() {
        final var chapter = domainFixture.chapterForUser(user.getId());
        final var task1 = domainFixture.taskForChapter(chapter.getId());
        final var task2 = domainFixture.taskForChapter(chapter.getId());

        final var response = request(chaptersUrl(chapter.getId()), HttpMethod.GET, Task[].class);
        assertionHelper.assertOk(response);
        assertNotNull(response.getBody());

        final List<Task> arcs = Arrays.stream(response.getBody()).toList();
        assertNotNull(arcs);
        assertEquals(2, arcs.size());
        assertThatCollection(arcs).containsExactly(task1, task2);
    }

    @Test
    void shouldReturnNotFound_whenChapterIdDoesNotExists() {
        final var response = request(chaptersUrl(ChapterId.random()), HttpMethod.GET, Void.class);

        assertionHelper.assertNotFound(response);
    }

    @Test
    void shouldReturnOk_whenChapterHasNoTasks() {
        final var chapter = domainFixture.chapterForUser(user.getId());
        final var response = request(chaptersUrl(chapter.getId()), HttpMethod.GET, Void.class);

        assertionHelper.assertOk(response);
    }

    @ParameterizedTest
    @MethodSource("provideTaskUpdateDtos")
    void shouldUpdateArc_withDifferentFields(final TaskUpdateDto dto) {
        final var task = domainFixture.taskForUser(user.getId());

        final var response = request(tasksUrl(task.getId()), HttpMethod.PUT, dto, Task.class);

        assertionHelper.assertOk(response);
        final var result = response.getBody();
        assertNotNull(result);

        assertEquals(task.getId(), result.getId());
        assertEquals(task.getChapter(), result.getChapter());

        assertEquals(assertionHelper.expectedValue(dto.estimatedMinutes(), task.getEstimatedMinutes()), result.getEstimatedMinutes());
        assertEquals(assertionHelper.expectedValue(dto.completedMinutes(), task.getCompletedMinutes()), result.getCompletedMinutes());
        assertEquals(assertionHelper.expectedValue(dto.scheduledAt(), task.getStartAt()), result.getStartAt());
        assertEquals(assertionHelper.expectedValue(dto.taskStatus(), task.getStatus()), result.getStatus());
    }

    private static Stream<Arguments> provideTaskUpdateDtos() {
        return Stream.of(
                Arguments.of(TaskUpdateDto.builder().completedMinutes(50).build()),
                Arguments.of(TaskUpdateDto.builder().estimatedMinutes(50).build()),
                Arguments.of(TaskUpdateDto.builder().scheduledAt(Instant.now().plus(7, ChronoUnit.DAYS)).build()),
                Arguments.of(TaskUpdateDto.builder().taskStatus(TaskStatus.SKIPPED).build()),
                Arguments.of(TaskUpdateDto.builder().build())
        );
    }

    @Test
    void shouldReturnNotFound_whenUpdatingNonExistingTask() {
        final var dto = aTaskUpdateDto();

        final var response = request(tasksUrl(TaskId.random()), HttpMethod.PUT,
                dto, Void.class);

        assertionHelper.assertNotFound(response);
    }

    @Test
    void shouldDeleteTask_whenIdExists() {
        final var task = domainFixture.taskForUser(user.getId());

        final var response = request(tasksUrl(task.getId()), HttpMethod.DELETE, Void.class);

        assertionHelper.assertNoContent(response);
    }

    @Test
    void shouldReturnNotFound_whenDeletingNonExistingTask() {
        final var response = request(tasksUrl(TaskId.random()), HttpMethod.DELETE, Void.class);

        assertionHelper.assertNotFound(response);
    }

    @Test
    void shouldDeleteAllTasksForChapter_whenChapterIdExists() {
        final var chapter = domainFixture.chapterForUser(user.getId());
        domainFixture.taskForChapter(chapter.getId());
        domainFixture.taskForChapter(chapter.getId());

        final var response = request(chaptersUrl(chapter.getId()), HttpMethod.DELETE, Void.class);

        assertionHelper.assertNoContent(response);
    }

    @Test
    void shouldReturnNotFound_whenDeletingAllTasksForNonExistingChapter() {
        final var response = request(chaptersUrl(ChapterId.random()), HttpMethod.DELETE, Void.class);

        assertionHelper.assertNotFound(response);
    }

    @Test
    void shouldReturnTodayTask_whenArcIsValidAndChapterExists() {
        final var arc = domainFixture.arcForUser(user.getId());
        final var chapter = domainFixture.chapterForArcWithDate(arc.getId(), LocalDate.now());
        final var task1 = domainFixture.taskForChapter(chapter.getId());
        final var task2 = domainFixture.taskForChapter(chapter.getId());


        final var response = exchangeTodayForUser(Task[].class);

        assertionHelper.assertOk(response);
        assertNotNull(response.getBody());

        final List<Task> tasks = Arrays.stream(response.getBody()).toList();
        assertThatCollection(tasks).containsExactly(task1, task2);
    }

    @Test
    void shouldThrowExceptionOnTodayTask_whenArcIsValidAndChapterDoesNotExists() {
        domainFixture.arcForUser(user.getId());

        final var response = exchangeTodayForUser(Void.class);

        assertionHelper.assertNotFound(response);
    }

    @Test
    void shouldThrowExceptionOnTodayTask_whenArcExistButNotActive() {
        domainFixture.arcForUser(user.getId(), ArcStatus.COMPLETED);

        final var response = exchangeTodayForUser(Void.class);

        assertionHelper.assertBadRequest(response);
    }

    @Test
    void shouldStartTask_whenTaskIsPending() {
        final var task = domainFixture.taskForUser(user.getId());

        final var response = request(tasksUrl(task.getId()) + "/start", HttpMethod.PATCH, Task.class);

        assertionHelper.assertOk(response);
        final var result = response.getBody();
        assertNotNull(result);
        assertEquals(TaskStatus.IN_PROGRESS, result.getStatus());
        assertNotNull(result.getStartedAt());
    }

    @ParameterizedTest
    @MethodSource("provideFinishedStatuses")
    void shouldReturnBadRequest_whenStartingTaskWithFinishedStatus(final TaskStatus status) {
        final var chapter = domainFixture.chapterForUser(user.getId());
        final var task = domainFixture.taskForChapterWithStatus(chapter.getId(), status);

        final var response = request(tasksUrl(task.getId()) + "/start", HttpMethod.PATCH, Void.class);

        assertionHelper.assertBadRequest(response);
    }

    private static Stream<Arguments> provideFinishedStatuses() {
        return Stream.of(
                Arguments.of(TaskStatus.DONE),
                Arguments.of(TaskStatus.SKIPPED)
        );
    }

    @ParameterizedTest
    @MethodSource("provideInvalidTaskEstimatedMinutes")
    void shouldReturnBadRequestOnCreate_whenEstimatedMinutesIsInvalid(final int minutes) {
        final var chapter = domainFixture.chapterForUser(user.getId());
        final var dto = aTaskCreationDtoWithChapterIdAndEstimatedMinutes(chapter.getId(), minutes);
        final var response = request(URL, HttpMethod.POST, dto, Void.class);
        assertionHelper.assertBadRequest(response);
    }

    @Test
    void shouldReturnBadRequestOnCreate_whenScheduledAtIsInPast() {
        final var chapter = domainFixture.chapterForUser(user.getId());
        final var dto = aTaskCreationDtoWithChapterIdAndScheduled(chapter.getId(),
                Instant.now().minusSeconds(60));
        final var response = request(URL, HttpMethod.POST, dto, Void.class);
        assertionHelper.assertBadRequest(response);
    }

    @ParameterizedTest
    @MethodSource("provideFinishedStatuses")
    void shouldReturnBadRequest_whenCompletingTaskWithFinishedStatus(final TaskStatus status) {
        final var chapter = domainFixture.chapterForUser(user.getId());
        final var task = domainFixture.taskForChapterWithStatus(chapter.getId(), status);
        final var dto = aTaskCompleteDto();

        final var response = request(tasksUrl(task.getId()) + "/complete", HttpMethod.PATCH, dto, Void.class);

        assertionHelper.assertBadRequest(response);
    }

    @ParameterizedTest
    @MethodSource("provideInvalidCompleteMinutes")
    void shouldReturnBadRequestOnComplete_whenCompletedMinutesIsInvalid(final int minutes) {
        final var task = domainFixture.taskWithChapter();
        final var dto = aTaskCompleteDtoWithMinutes(minutes);
        final var response = request(tasksUrl(task.getId()) + "/complete", HttpMethod.PATCH,
                dto, Void.class);
        assertionHelper.assertBadRequest(response);
    }

    private static Stream<Arguments> provideInvalidTaskEstimatedMinutes() {
        return Stream.of(
                Arguments.of(0),
                Arguments.of(-1),
                Arguments.of(1441)
        );
    }

    private static Stream<Arguments> provideInvalidCompleteMinutes() {
        return Stream.of(
                Arguments.of(-1),
                Arguments.of(1441)
        );
    }

    // BASE is 1 hour in the future — guarantees @FutureOrPresent passes for all overlap tests
    private static final Instant BASE = Instant.now().plus(1, ChronoUnit.HOURS).truncatedTo(ChronoUnit.MILLIS);

    @Test
    void shouldReturnBadRequest_whenCreatingTaskOverlapsWithExistingTask() {
        // existing [BASE, BASE+60min], new [BASE+30min, BASE+90min] → overlaps
        final var chapter = domainFixture.chapterForUser(user.getId());
        domainFixture.taskForChapterAtTime(chapter.getId(), BASE, 60);

        final var dto = aTaskCreationDtoWithChapterIdAndWindow(chapter.getId(), BASE.plus(30, ChronoUnit.MINUTES), 60);
        final var response = request(URL, HttpMethod.POST, dto, Void.class);

        assertionHelper.assertBadRequest(response);
    }

    @Test
    void shouldReturnBadRequest_whenUpdatingScheduledAtOverlapsWithAnotherTask() {
        // taskA [BASE+2h, BASE+3h], taskB [BASE+4h, BASE+5h]
        // update taskB scheduledAt to BASE+2h30m → [BASE+2h30m, BASE+3h30m] overlaps with taskA
        final var chapter = domainFixture.chapterForUser(user.getId());
        domainFixture.taskForChapterAtTime(chapter.getId(), BASE.plus(2, ChronoUnit.HOURS), 60);
        final var taskB = domainFixture.taskForChapterAtTime(chapter.getId(), BASE.plus(4, ChronoUnit.HOURS), 60);

        final var dto = TaskUpdateDto.builder().scheduledAt(BASE.plus(2, ChronoUnit.HOURS).plus(30, ChronoUnit.MINUTES)).build();
        final var response = request(tasksUrl(taskB.getId()), HttpMethod.PUT, dto, Void.class);

        assertionHelper.assertBadRequest(response);
    }

    @Test
    void shouldReturnBadRequest_whenUpdatingEstimatedMinutesOverlapsWithAnotherTask() {
        // taskA [BASE+2h, BASE+3h], taskB [BASE+1h, BASE+1h30m] (30 min, no overlap)
        // update taskB estimatedMinutes to 90 → [BASE+1h, BASE+2h30m] overlaps with taskA
        final var chapter = domainFixture.chapterForUser(user.getId());
        domainFixture.taskForChapterAtTime(chapter.getId(), BASE.plus(2, ChronoUnit.HOURS), 60);
        final var taskB = domainFixture.taskForChapterAtTime(chapter.getId(), BASE.plus(1, ChronoUnit.HOURS), 30);

        final var dto = TaskUpdateDto.builder().estimatedMinutes(90).build();
        final var response = request(tasksUrl(taskB.getId()), HttpMethod.PUT, dto, Void.class);

        assertionHelper.assertBadRequest(response);
    }

    @Test
    void shouldReturnOk_whenUpdatingOwnScheduledAtWithoutOverlappingOtherTasks() {
        // taskA [BASE, BASE+1h], taskB [BASE+2h, BASE+3h]
        // update taskB scheduledAt to BASE+3h → [BASE+3h, BASE+4h] no overlap, self-exclusion works
        final var chapter = domainFixture.chapterForUser(user.getId());
        domainFixture.taskForChapterAtTime(chapter.getId(), BASE, 60);
        final var taskB = domainFixture.taskForChapterAtTime(chapter.getId(), BASE.plus(2, ChronoUnit.HOURS), 60);

        final var dto = TaskUpdateDto.builder().scheduledAt(BASE.plus(3, ChronoUnit.HOURS)).build();
        final var response = request(tasksUrl(taskB.getId()), HttpMethod.PUT, dto, Task.class);

        assertionHelper.assertOk(response);
    }
}