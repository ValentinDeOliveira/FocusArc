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

        final var dto = aTaskCreationDtoWithChapterId(chapter.getId());
        final var response = request(URL, HttpMethod.POST, getHttpEntity(dto), Task.class);

        assertionHelper.assertCreated(response);

        final var task = response.getBody();
        assertNotNull(task);

        // TODO: create assertion class for DTO
        assertEquals(dto.scheduledAt(), task.getScheduledAt());
        assertEquals(dto.chapterId(), task.getChapter());
        assertEquals(dto.estimatedMinutes(), task.getEstimatedMinutes());
        assertEquals(0, task.getCompletedMinutes());
        assertEquals(TaskStatus.PLANNED, task.getStatus());
        assertNotNull(task.getId());
    }

    @Test
    void shouldReturnNotFoundOnCreate_whenChapterDoesNotExists() {
        final var dto = aTaskCreationDto();

        final var response = request(URL, HttpMethod.POST, getHttpEntity(dto), Void.class);

        assertionHelper.assertNotFound(response);
    }

    @Test
    void shouldReturnTask_whenIdExists() {
        final var task = domainFixture.taskForUser(user.getId());

        final var response = request(URL + "/" + task.getId().id(), HttpMethod.GET,
                getHttpEntity(), Task.class);
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

        final var response = request(URL + "/chapters/" + chapter.getId().id(), HttpMethod.GET,
                getHttpEntity(), Task[].class);
        assertionHelper.assertOk(response);
        assertNotNull(response.getBody());

        final List<Task> arcs = Arrays.stream(response.getBody()).toList();
        assertNotNull(arcs);
        assertEquals(2, arcs.size());
        assertThatCollection(arcs).containsExactly(task1, task2);
    }

    @Test
    void shouldReturnNotFound_whenChapterIdDoesNotExists() {
        final var response = request(URL + "/chapters/" + ChapterId.random().id(), HttpMethod.GET,
                getHttpEntity(), Void.class);

        assertionHelper.assertNotFound(response);
    }

    @Test
    void shouldReturnNoContent_whenChapterHasNoTasks() {
        final var chapter = domainFixture.chapterForUser(user.getId());
        final var response = request(URL + "/chapters/" + chapter.getId().id(), HttpMethod.GET,
                getHttpEntity(), Void.class);

        assertionHelper.assertNoContent(response);
    }

    @ParameterizedTest
    @MethodSource("provideTaskUpdateDtos")
    void shouldUpdateArc_withDifferentFields(final TaskUpdateDto dto) {
        final var task = domainFixture.taskForUser(user.getId());

        final var response = request(URL + "/" + task.getId().id(), HttpMethod.PUT,
                getHttpEntity(dto), Task.class);

        assertionHelper.assertOk(response);
        final var result = response.getBody();
        assertNotNull(result);

        assertEquals(task.getId(), result.getId());
        assertEquals(task.getChapter(), result.getChapter());

        assertEquals(assertionHelper.expectedValue(dto.estimatedMinutes(), task.getEstimatedMinutes()), result.getEstimatedMinutes());
        assertEquals(assertionHelper.expectedValue(dto.completedMinutes(), task.getCompletedMinutes()), result.getCompletedMinutes());
        assertEquals(assertionHelper.expectedValue(dto.scheduledAt(), task.getScheduledAt()), result.getScheduledAt());
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

        final var response = request(URL + "/" + TaskId.random().id(), HttpMethod.PUT,
                getHttpEntity(dto), Void.class);

        assertionHelper.assertNotFound(response);
    }

    @Test
    void shouldDeleteTask_whenIdExists() {
        final var task = domainFixture.taskForUser(user.getId());

        final var response = request(URL + "/" + task.getId().id(), HttpMethod.DELETE,
                getHttpEntity(), Void.class);

        assertionHelper.assertNoContent(response);
    }

    @Test
    void shouldReturnNotFound_whenDeletingNonExistingTask() {
        final var response = request(URL + "/" + TaskId.random().id(), HttpMethod.DELETE,
                getHttpEntity(), Void.class);

        assertionHelper.assertNotFound(response);
    }

    @Test
    void shouldDeleteAllTasksForChapter_whenChapterIdExists() {
        final var chapter = domainFixture.chapterForUser(user.getId());
        domainFixture.taskForChapter(chapter.getId());
        domainFixture.taskForChapter(chapter.getId());

        final var response = request(URL + "/chapters/" + chapter.getId().id(), HttpMethod.DELETE,
                getHttpEntity(), Void.class);

        assertionHelper.assertNoContent(response);
    }

    @Test
    void shouldReturnNotFound_whenDeletingAllTasksForNonExistingChapter() {
        final var response = request(URL + "/chapters/" + ChapterId.random().id(), HttpMethod.DELETE,
                getHttpEntity(), Void.class);

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

    @ParameterizedTest
    @MethodSource("provideInvalidTaskEstimatedMinutes")
    void shouldReturnBadRequestOnCreate_whenEstimatedMinutesIsInvalid(final int minutes) {
        final var chapter = domainFixture.chapterForUser(user.getId());
        final var dto = aTaskCreationDtoWithChapterIdAndEstimatedMinutes(chapter.getId(), minutes);
        final var response = request(URL, HttpMethod.POST, getHttpEntity(dto), Void.class);
        assertionHelper.assertBadRequest(response);
    }

    @Test
    void shouldReturnBadRequestOnCreate_whenScheduledAtIsInPast() {
        final var chapter = domainFixture.chapterForUser(user.getId());
        final var dto = aTaskCreationDtoWithChapterIdAndScheduled(chapter.getId(),
                Instant.now().minusSeconds(60));
        final var response = request(URL, HttpMethod.POST, getHttpEntity(dto), Void.class);
        assertionHelper.assertBadRequest(response);
    }

    @ParameterizedTest
    @MethodSource("provideInvalidCompleteMinutes")
    void shouldReturnBadRequestOnComplete_whenCompletedMinutesIsInvalid(final int minutes) {
        final var task = domainFixture.taskWithChapter();
        final var dto = aTaskCompleteDtoWithMinutes(minutes);
        final var response = request(URL + "/" + task.getId().id() + "/complete", HttpMethod.PATCH,
                getHttpEntity(dto), Void.class);
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
}