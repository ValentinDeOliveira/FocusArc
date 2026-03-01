package com.valentin_d.focusarc.integration;

import com.valentin_d.focusarc.dto.task.TaskUpdateDto;
import com.valentin_d.focusarc.integration.base.BaseTaskControllerIntegrationTest;
import com.valentin_d.focusarc.model.arc.ArcStatus;
import com.valentin_d.focusarc.model.id.ChapterId;
import com.valentin_d.focusarc.model.id.TaskId;
import com.valentin_d.focusarc.model.id.UserId;
import com.valentin_d.focusarc.model.task.Task;
import com.valentin_d.focusarc.model.task.TaskStatus;
import com.valentin_d.focusarc.repository.ArcRepository;
import com.valentin_d.focusarc.repository.UserRepository;
import com.valentin_d.focusarc.service.chapter.ChapterRecalculationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import static com.valentin_d.focusarc.fixtures.factory.ArcFactory.anArcWithOwnerId;
import static com.valentin_d.focusarc.fixtures.factory.ArcFactory.anArcWithOwnerIdAndStatus;
import static com.valentin_d.focusarc.fixtures.factory.ChapterFactory.aChapterWithScheduledDateAndArcId;
import static com.valentin_d.focusarc.fixtures.factory.TaskFactory.*;
import static com.valentin_d.focusarc.fixtures.factory.UserFactory.aUser;
import static org.assertj.core.api.CollectionAssert.assertThatCollection;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TaskControllerIntegrationTest extends BaseTaskControllerIntegrationTest {
    @MockitoBean
    private ChapterRecalculationService chapterRecalculationService;
    @Autowired
    private ArcRepository arcRepository;
    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        arcRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    void shouldCreateTask_whenDataIsValid() {
        final var chapter = createChapter();

        final var dto = aTaskCreationDtoWithChapterId(chapter.getId());
        final var response = request(URL, HttpMethod.POST, dto, Task.class);

        assertCreated(response);

        final var task = response.getBody();
        assertNotNull(task);

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

        final var response = request(URL, HttpMethod.POST, dto, Void.class);

        assertNotFound(response);
    }

    @Test
    void shouldReturnTask_whenIdExists() {
        final var task = createTask();

        final var response = request(URL + "/" + task.getId().id(), HttpMethod.GET, Task.class);
        assertOk(response);

        final var result = response.getBody();
        assertNotNull(result);

        assertTasksEquals(result, task);
    }

    @Test
    void shouldReturnAllTask_whenArcChapterExists() {
        final var chapter = createChapter();
        final var task1 = createTaskForChapter(chapter.getId());
        final var task2 = createTaskForChapter(chapter.getId());

        final var response = request(URL + "/chapters/" + chapter.getId().id(), HttpMethod.GET, Task[].class);
        assertOk(response);
        assertNotNull(response.getBody());

        final List<Task> arcs = Arrays.stream(response.getBody()).toList();
        assertNotNull(arcs);
        assertEquals(2, arcs.size());
        assertThatCollection(arcs).containsExactly(task1, task2);
    }

    @Test
    void shouldReturnNotFound_whenChapterIdDoesNotExists() {
        final var response = request(URL + "/chapters/" + ChapterId.random().id(), HttpMethod.GET, Void.class);

        assertNotFound(response);
    }

    @Test
    void shouldReturnNoContent_whenChapterHasNoTasks() {
        final var chapter = createChapter();
        final var response = request(URL + "/chapters/" + chapter.getId().id(), HttpMethod.GET, Void.class);

        assertNoContent(response);
    }

    @ParameterizedTest
    @MethodSource("provideTaskUpdateDtos")
    void shouldUpdateArc_withDifferentFields(final TaskUpdateDto dto) {
        final var task = createTask();

        final var response = request(URL + "/" + task.getId().id(), HttpMethod.PUT, dto, Task.class);

        assertOk(response);
        final var result = response.getBody();
        assertNotNull(result);

        assertEquals(task.getId(), result.getId());
        assertEquals(task.getChapter(), result.getChapter());

        assertEquals(expectedValue(dto.estimatedMinutes(), task.getEstimatedMinutes()), result.getEstimatedMinutes());
        assertEquals(expectedValue(dto.completedMinutes(), task.getCompletedMinutes()), result.getCompletedMinutes());
        assertEquals(expectedValue(dto.scheduledAt(), task.getScheduledAt()), result.getScheduledAt());
        assertEquals(expectedValue(dto.taskStatus(), task.getStatus()), result.getStatus());
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

        final var response = request(URL + "/" + TaskId.random().id(), HttpMethod.PUT, dto, Void.class);

        assertNotFound(response);
    }

    @Test
    void shouldDeleteTask_whenIdExists() {
        final var task = createTask();

        final var response = request(URL + "/" + task.getId().id(), HttpMethod.DELETE, Void.class);

        assertNoContent(response);
    }

    @Test
    void shouldReturnNotFound_whenDeletingNonExistingTask() {
        final var response = request(URL + "/" + TaskId.random().id(), HttpMethod.DELETE, Void.class);

        assertNotFound(response);
    }

    @Test
    void shouldDeleteAllTasksForChapter_whenChapterIdExists() {
        final var chapter = createChapter();
        createTaskForChapter(chapter.getId());
        createTaskForChapter(chapter.getId());

        final var response = request(URL + "/chapters/" + chapter.getId().id(), HttpMethod.DELETE, Void.class);

        assertNoContent(response);
    }

    @Test
    void shouldReturnNotFound_whenDeletingAllTasksForNonExistingChapter() {
        final var response = request(URL + "/chapters/" + ChapterId.random().id(), HttpMethod.DELETE, Void.class);

        assertNotFound(response);
    }

    @Test
    void shouldReturnTodayTask_whenArcIsValidAndChapterExists() {
        final var user = userRepository.save(aUser());
        final var arc = arcRepository.save(anArcWithOwnerId(user.getId()));
        final var chapter = chapterRepository.save(aChapterWithScheduledDateAndArcId(LocalDate.now(), arc.getId()));
        final var task1 = createTaskForChapter(chapter.getId());
        final var task2 = createTaskForChapter(chapter.getId());

        final var response = request(URL + "/today?userId=" + user.getId().id(), HttpMethod.GET, Task[].class);
        assertOk(response);
        assertNotNull(response.getBody());

        final List<Task> tasks = Arrays.stream(response.getBody()).toList();
        assertThatCollection(tasks).containsExactly(task1, task2);
    }

    @Test
    void shouldThrowExceptionOnTodayTask_whenArcIsValidAndChapterDoesNotExists() {
        final var user = userRepository.save(aUser());
        arcRepository.save(anArcWithOwnerId(user.getId()));

        final var response = request(URL + "/today?userId=" + user.getId().id(), HttpMethod.GET, Void.class);
        assertNotFound(response);
    }

    @Test
    void shouldThrowExceptionOnTodayTask_whenArcExistButNotActive() {
        final var user = userRepository.save(aUser());
        final var arc = anArcWithOwnerIdAndStatus(user.getId(), ArcStatus.COMPLETED);
        arcRepository.save(arc);

        final var response = request(URL + "/today?userId=" + user.getId().id(), HttpMethod.GET, Void.class);
        assertBadRequest(response);
    }

    @Test
    void shouldThrowExceptionOnTodayTask_whenUserDoesNotExist() {
        final var response = request(URL + "/today?userId=" + UserId.random().id(), HttpMethod.GET, Void.class);
        assertNotFound(response);
    }
}