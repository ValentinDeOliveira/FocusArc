package com.valentin_d.focusarc.service;

import com.valentin_d.focusarc.dto.task.TaskCompleteDto;
import com.valentin_d.focusarc.exception.arc.ArcDoesNotExistForUserException;
import com.valentin_d.focusarc.exception.chapter.ChapterDoesNotExistException;
import com.valentin_d.focusarc.exception.task.TaskAlreadyFinishedException;
import com.valentin_d.focusarc.exception.task.TaskDoesNotExistException;
import com.valentin_d.focusarc.exception.task.TaskInvalidMinuteException;
import com.valentin_d.focusarc.exception.user.UserDoesNotExistException;
import com.valentin_d.focusarc.model.id.ArcId;
import com.valentin_d.focusarc.model.id.ChapterId;
import com.valentin_d.focusarc.model.id.UserId;
import com.valentin_d.focusarc.model.task.Task;
import com.valentin_d.focusarc.model.task.TaskStatus;
import com.valentin_d.focusarc.repository.TaskRepository;
import com.valentin_d.focusarc.service.chapter.ChapterRecalculationService;
import com.valentin_d.focusarc.service.tag.TagLoader;
import com.valentin_d.focusarc.service.task.TaskLoader;
import com.valentin_d.focusarc.service.task.TaskService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.util.List;
import java.util.stream.Stream;

import static com.valentin_d.focusarc.fixtures.factory.ArcFactory.anArc;
import static com.valentin_d.focusarc.fixtures.factory.ChapterFactory.aChapter;
import static com.valentin_d.focusarc.fixtures.factory.ChapterFactory.aChapterWithArcId;
import static com.valentin_d.focusarc.fixtures.factory.TaskFactory.*;
import static com.valentin_d.focusarc.fixtures.factory.UserFactory.aUser;
import static com.valentin_d.focusarc.shared.TimeConstraints.MINUTES_PER_DAY;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private ChapterRecalculationService chapterRecalculationService;
    @Mock
    private TaskLoader taskLoader;
    @Mock
    private ContextLoader contextLoader;
    @Mock
    private TagLoader tagLoader;
    @InjectMocks
    private TaskService service;

    @Test
    void shouldCreateTask_whenChapterExist() {
        final var user = aUser();
        final var creationDto = aTaskCreationDto();

        when(taskRepository.save(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        final var result = service.create(creationDto, user.getId());

        assertEquals(creationDto.chapterId(), result.getChapter());
        assertEquals(creationDto.estimatedMinutes(), result.getEstimatedMinutes());
        assertEquals(0, result.getCompletedMinutes());
        assertEquals(creationDto.scheduledAt(), result.getStartAt());
        assertEquals(creationDto.name(), result.getName());
        assertEquals(creationDto.description(), result.getDescription());

        verify(contextLoader).assertChapterForUser(creationDto.chapterId(), user.getId());
        verify(tagLoader).assertTagsForUser(user.getId(), creationDto.tagId());
        verify(taskLoader).existForChapterAtTime(eq(creationDto.chapterId()), eq(creationDto.scheduledAt()), any(Instant.class));
        verify(taskRepository).save(any(Task.class));
        verify(chapterRecalculationService).recalculateEstimatedMinutes(creationDto.chapterId());
    }

    @Test
    void shouldThrowExceptionOnCreate_whenChapterDoesNotExist() {
        final var userId = UserId.random();
        final var creationDto = aTaskCreationDto();

        doThrowChapterDoesNotExist(creationDto.chapterId(), userId);

        assertThatThrownBy(() -> service.create(creationDto, userId))
                .isInstanceOf(ChapterDoesNotExistException.class)
                .hasMessageContaining(String.valueOf(creationDto.chapterId().id().toString()));

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void shouldUpdate_whenTaskExists() {
        final var user = aUser();
        final var task = aTask();
        final var updateDto = aTaskUpdateDto();

        when(taskLoader.getTaskIfExists(task.getId())).thenReturn(task);

        when(taskRepository.save(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        final var updated = service.update(task.getId(), updateDto, user.getId());

        verify(contextLoader).assertChapterForUser(task.getChapter(), user.getId());
        verify(tagLoader).assertTagsForUser(user.getId(), updateDto.tag());
        verify(taskLoader).existForChapterAtTimeExcluding(eq(task.getChapter()), eq(task.getId()),
                eq(updateDto.scheduledAt()), any(Instant.class));
        verify(taskRepository).save(task);

        assertEquals(updated.getId(), task.getId());
        assertEquals(updated.getEstimatedMinutes(), updateDto.estimatedMinutes());
        assertEquals(updated.getCompletedMinutes(), updateDto.completedMinutes());
        assertEquals(updated.getStartAt(), updateDto.scheduledAt());
        assertEquals(updated.getChapter(), task.getChapter());
        assertEquals(updated.getName(), task.getName());
        assertEquals(updated.getDescription(), task.getDescription());
    }

    @Test
    void shouldThrowExceptionOnUpdate_whenTaskDoesNotExists() {
        final var task = aTask();
        final var updateDto = aTaskUpdateDto();

        when(taskLoader.getTaskIfExists(eq(task.getId())))
                .thenThrow((new TaskDoesNotExistException(task.getId())));

        assertThatThrownBy(() -> service.update(task.getId(), updateDto, UserId.random()))
                .isInstanceOf(TaskDoesNotExistException.class)
                .hasMessageContaining(String.valueOf(task.getId().id()));

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void shouldDeleteTask_whenTaskExists() {
        final var user = aUser();
        final var task = aTask();

        when(taskLoader.getTaskIfExists(task.getId())).thenReturn(task);

        service.delete(task.getId(), user.getId());

        verify(contextLoader).assertChapterForUser(task.getChapter(), user.getId());
        verify(taskRepository).delete(task);
        verify(chapterRecalculationService).recalculateEstimatedMinutes(task.getChapter());
        verify(chapterRecalculationService).recalculateCompletedMinutes(task.getChapter());
    }

    @Test
    void shouldThrowExceptionOnDelete_whenTaskDoesNotExists() {
        final var task = aTask();

        when(taskLoader.getTaskIfExists(eq(task.getId())))
                .thenThrow((new TaskDoesNotExistException(task.getId())));

        assertThatThrownBy(() -> service.delete(task.getId(), UserId.random()))
                .isInstanceOf(TaskDoesNotExistException.class)
                .hasMessageContaining(String.valueOf(task.getId().id()));

        verify(taskRepository, never()).delete(any(Task.class));
    }

    @Test
    void shouldDeleteAllTasks_whenChapterExists() {
        final var user = aUser();
        final var chapter = aChapter();
        final var task = aTaskWithChapterId(chapter.getId());

        when(taskRepository.findAllByChapter(chapter.getId())).thenReturn(List.of(task));

        service.deleteAllForChapter(chapter.getId(), user.getId());

        verify(contextLoader).assertChapterForUser(task.getChapter(), user.getId());
        verify(taskRepository).deleteAll(List.of(task));
        verify(chapterRecalculationService).recalculateEstimatedMinutes(task.getChapter());
        verify(chapterRecalculationService).recalculateCompletedMinutes(task.getChapter());
    }

    @Test
    void shouldThrowExceptionOnDeleteAllTasks_whenChapterDoesNotExists() {
        final var userId = UserId.random();
        final var chapter = aChapter();

        doThrowChapterDoesNotExist(chapter.getId(), userId);

        assertThatThrownBy(() -> service.deleteAllForChapter(chapter.getId(), userId))
                .isInstanceOf(ChapterDoesNotExistException.class)
                .hasMessageContaining(String.valueOf(chapter.getId().id().toString()));

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void shouldGetAllTasksForChapter_whenChapterExists() {
        final var user = aUser();
        final var chapter = aChapter();
        final var task = aTaskWithChapterId(chapter.getId());
        when(taskRepository.findAllByChapter(chapter.getId())).thenReturn(List.of(task));

        final var result = service.findAllForChapter(chapter.getId(), user.getId());

        verify(contextLoader).assertChapterForUser(task.getChapter(), user.getId());

        assertEquals(List.of(task), result);
    }

    @Test
    void shouldThrowExceptionOnGetAllTasksForChapter_whenChapterDoesNotExists() {
        final var userId = UserId.random();
        final var chapter = aChapter();

        doThrowChapterDoesNotExist(chapter.getId(), userId);

        assertThatThrownBy(() -> service.findAllForChapter(chapter.getId(), userId))
                .isInstanceOf(ChapterDoesNotExistException.class)
                .hasMessageContaining(String.valueOf(chapter.getId().id().toString()));

        verify(taskRepository, never()).findAllByChapter(chapter.getId());
    }

    @Test
    void shouldCompleteTask_whenTaskExistsAndNotDone() {
        final var user = aUser();
        final var task = aTask();
        final var dto = aTaskCompleteDto();

        when(taskLoader.getTaskIfExists(task.getId())).thenReturn(task);
        when(taskRepository.save(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.completeTask(task.getId(), user.getId(), dto);

        final var captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(captor.capture());

        final var savedTask = captor.getValue();

        assertEquals(TaskStatus.DONE, savedTask.getStatus());
        assertEquals(dto.completedMinutes(), savedTask.getCompletedMinutes());
        assertNotNull(savedTask.getCompletedAt());

        verify(contextLoader).assertChapterForUser(savedTask.getChapter(), user.getId());
        verify(chapterRecalculationService).recalculateCompletedMinutes(task.getChapter());
    }

    @ParameterizedTest
    @MethodSource("provideFinishedTaskStatuses")
    void shouldThrowExceptionOnComplete_whenTaskIsFinished(final TaskStatus status) {
        final var task = aTaskWithStatus(status);
        final var dto = aTaskCompleteDto();

        when(taskLoader.getTaskIfExists(task.getId())).thenReturn(task);

        assertThatThrownBy(() -> service.completeTask(task.getId(), UserId.random(), dto))
                .isInstanceOf(TaskAlreadyFinishedException.class)
                .hasMessageContaining(String.valueOf(task.getId().id()))
                .hasMessageContaining(status.name());

        verify(taskRepository, never()).save(any());
        verify(chapterRecalculationService, never()).recalculateCompletedMinutes(any());
    }

    @ParameterizedTest
    @MethodSource("provideTaskCompleteDtos")
    void shouldThrowExceptionOnComplete_whenDtoDataIsInvalid(final TaskCompleteDto dto) {
        final var task = aTask();

        when(taskLoader.getTaskIfExists(task.getId())).thenReturn(task);

        assertThatThrownBy(() -> service.completeTask(task.getId(), UserId.random(), dto))
                .isInstanceOf(TaskInvalidMinuteException.class)
                .hasMessageContaining(String.valueOf(task.getId().id()))
                .hasMessageContaining(String.valueOf(dto.completedMinutes()));

        verify(taskRepository, never()).save(any());
        verify(chapterRecalculationService, never()).recalculateCompletedMinutes(any());
    }

    @Test
    void shouldReturnTodayTasks_whenArcIsValidAndChapterExists() {
        final var userId = UserId.random();
        final var arc = anArc();
        final var chapter = aChapterWithArcId(arc.getId());
        final var tasks = List.of(aTaskWithChapterId(chapter.getId()));

        when(contextLoader.getChapterFromUserId(userId)).thenReturn(chapter);
        when(taskLoader.getTasksForChapter(chapter.getId())).thenReturn(tasks);

        final var result = service.getTodaysTasks(userId);

        assertEquals(tasks, result);
    }

    @Test
    void shouldStartTask_whenTaskExistsAndIsPending() {
        final var user = aUser();
        final var task = aTaskWithStatus(TaskStatus.PLANNED);

        when(taskLoader.getTaskIfExists(task.getId())).thenReturn(task);
        when(taskRepository.save(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        final var result = service.startTask(task.getId(), user.getId());

        assertEquals(TaskStatus.IN_PROGRESS, result.getStatus());
        assertNotNull(result.getStartedAt());

        verify(contextLoader).assertChapterForUser(task.getChapter(), user.getId());
        assertEquals(task.getId(), result.getId());
    }

    @ParameterizedTest
    @MethodSource("provideFinishedTaskStatuses")
    void shouldThrowExceptionOnStart_whenTaskIsFinished(final TaskStatus status) {
        final var task = aTaskWithStatus(status);

        when(taskLoader.getTaskIfExists(task.getId())).thenReturn(task);

        assertThatThrownBy(() -> service.startTask(task.getId(), UserId.random()))
                .isInstanceOf(TaskAlreadyFinishedException.class)
                .hasMessageContaining(task.getId().id().toString())
                .hasMessageContaining(status.name());

        verify(taskRepository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionOnStart_whenUserDoesNotOwnChapter() {
        final var attacker = aUser();
        final var task = aTaskWithStatus(TaskStatus.PLANNED);

        when(taskLoader.getTaskIfExists(task.getId())).thenReturn(task);
        doThrowArcDoesNotExistForUser(task.getChapter(), attacker.getId());

        assertThatThrownBy(() -> service.startTask(task.getId(), attacker.getId()))
                .isInstanceOf(ArcDoesNotExistForUserException.class);

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void shouldThrowErrorOnTodayTasks_whenArcIsValidAndChapterDoesNotExists() {
        final var userId = UserId.random();

        doThrow(new UserDoesNotExistException(userId))
                .when(contextLoader)
                .getChapterFromUserId(eq(userId));

        assertThatThrownBy(() -> service.getTodaysTasks(userId))
                .isInstanceOf(UserDoesNotExistException.class)
                .hasMessageContaining(String.valueOf(userId.id()));

        verify(contextLoader).getChapterFromUserId(userId);
        verify(taskLoader, never()).getTasksForChapter(any(ChapterId.class));
    }

    private static Stream<Arguments> provideFinishedTaskStatuses() {
        return Stream.of(
                Arguments.of(TaskStatus.DONE),
                Arguments.of(TaskStatus.SKIPPED)
        );
    }

    private static Stream<Arguments> provideTaskCompleteDtos() {
        return Stream.of(
                Arguments.of(aTaskCompleteDtoWithMinutes(-10)),
                Arguments.of(aTaskCompleteDtoWithMinutes(MINUTES_PER_DAY + 1))
            );
    }

    @Test
    void shouldThrowExceptionOnCreate_whenUserDoesNotOwnChapter() {
        final var attacker = aUser();
        final var creationDto = aTaskCreationDto();

        doThrowArcDoesNotExistForUser(creationDto.chapterId(), attacker.getId());

        assertThatThrownBy(() -> service.create(creationDto, attacker.getId()))
                .isInstanceOf(ArcDoesNotExistForUserException.class);

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void shouldThrowExceptionOnUpdate_whenUserDoesNotOwnChapter() {
        final var attacker = aUser();
        final var task = aTask();
        final var updateDto = aTaskUpdateDto();

        when(taskLoader.getTaskIfExists(task.getId())).thenReturn(task);
        doThrowArcDoesNotExistForUser(task.getChapter(), attacker.getId());

        assertThatThrownBy(() -> service.update(task.getId(), updateDto, attacker.getId()))
                .isInstanceOf(ArcDoesNotExistForUserException.class);

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void shouldThrowExceptionOnDelete_whenUserDoesNotOwnChapter() {
        final var attacker = aUser();
        final var task = aTask();

        when(taskLoader.getTaskIfExists(task.getId())).thenReturn(task);
        doThrowArcDoesNotExistForUser(task.getChapter(), attacker.getId());

        assertThatThrownBy(() -> service.delete(task.getId(), attacker.getId()))
                .isInstanceOf(ArcDoesNotExistForUserException.class);

        verify(taskRepository, never()).delete(any(Task.class));
    }

    @Test
    void shouldThrowExceptionOnDeleteAll_whenUserDoesNotOwnChapter() {
        final var attacker = aUser();
        final var chapter = aChapter();

        doThrowArcDoesNotExistForUser(chapter.getId(), attacker.getId());

        assertThatThrownBy(() -> service.deleteAllForChapter(chapter.getId(), attacker.getId()))
                .isInstanceOf(ArcDoesNotExistForUserException.class);

        verify(taskRepository, never()).deleteAll(anyList());
    }

    @Test
    void shouldThrowExceptionOnGetAll_whenUserDoesNotOwnChapter() {
        final var attacker = aUser();
        final var chapter = aChapter();

        doThrowArcDoesNotExistForUser(chapter.getId(), attacker.getId());

        assertThatThrownBy(() -> service.findAllForChapter(chapter.getId(), attacker.getId()))
                .isInstanceOf(ArcDoesNotExistForUserException.class);

        verify(taskRepository, never()).findAllByChapter(any(ChapterId.class));
    }

    @Test
    void shouldThrowExceptionOnComplete_whenUserDoesNotOwnChapter() {
        final var attacker = aUser();
        final var task = aTask();
        final var dto = aTaskCompleteDto();

        when(taskLoader.getTaskIfExists(task.getId())).thenReturn(task);
        doThrowArcDoesNotExistForUser(task.getChapter(), attacker.getId());

        assertThatThrownBy(() -> service.completeTask(task.getId(), attacker.getId(), dto))
                .isInstanceOf(ArcDoesNotExistForUserException.class);

        verify(taskRepository, never()).save(any(Task.class));
    }

    private void doThrowChapterDoesNotExist(final ChapterId chapterId, final UserId userId) {
        doThrow(new ChapterDoesNotExistException(chapterId))
                .when(contextLoader)
                .assertChapterForUser(chapterId, userId);
    }

    private void doThrowArcDoesNotExistForUser(final ChapterId chapterId, final UserId userId) {
        doThrow(new ArcDoesNotExistForUserException(ArcId.random(), userId))
                .when(contextLoader)
                .assertChapterForUser(chapterId, userId);
    }
}