package com.valentin_d.focusarc.service;

import com.valentin_d.focusarc.dto.task.TaskCompleteDto;
import com.valentin_d.focusarc.exception.ChapterDoesNotExistException;
import com.valentin_d.focusarc.exception.NoActiveArcException;
import com.valentin_d.focusarc.exception.NoChapterForArcException;
import com.valentin_d.focusarc.exception.task.TaskAlreadyDoneException;
import com.valentin_d.focusarc.exception.task.TaskDoesNotExistException;
import com.valentin_d.focusarc.exception.task.TaskInvalidMinuteException;
import com.valentin_d.focusarc.model.id.ArcId;
import com.valentin_d.focusarc.model.id.ChapterId;
import com.valentin_d.focusarc.model.id.UserId;
import com.valentin_d.focusarc.model.task.Task;
import com.valentin_d.focusarc.model.task.TaskStatus;
import com.valentin_d.focusarc.repository.TaskRepository;
import com.valentin_d.focusarc.service.arc.ArcLoader;
import com.valentin_d.focusarc.service.chapter.ChapterLoader;
import com.valentin_d.focusarc.service.chapter.ChapterRecalculationService;
import com.valentin_d.focusarc.service.task.TaskLoader;
import com.valentin_d.focusarc.service.task.TaskService;
import com.valentin_d.focusarc.service.user.UserLoader;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static com.valentin_d.focusarc.fixtures.factory.ArcFactory.anArc;
import static com.valentin_d.focusarc.fixtures.factory.ChapterFactory.aChapter;
import static com.valentin_d.focusarc.fixtures.factory.ChapterFactory.aChapterWithArcId;
import static com.valentin_d.focusarc.fixtures.factory.TaskFactory.*;
import static com.valentin_d.focusarc.shared.TimeConstraints.MINUTES_PER_DAY;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
    private ChapterLoader chapterLoader;
    @Mock
    private ArcLoader arcLoader;
    @Mock
    private UserLoader userLoader;
    @InjectMocks
    private TaskService service;

    @Test
    void shouldCreateTask_whenChapterExist() {
        final var creationDto = aTaskCreationDto();

        when(taskRepository.save(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        final var result = service.create(creationDto);

        assertEquals(creationDto.chapterId(), result.getChapter());
        assertEquals(creationDto.estimatedMinutes(), result.getEstimatedMinutes());
        assertEquals(0, result.getCompletedMinutes());
        assertEquals(creationDto.scheduledAt(), result.getScheduledAt());

        verify(taskRepository).save(any(Task.class));
        verify(chapterRecalculationService).recalculateEstimatedMinutes(creationDto.chapterId());
    }

    @Test
    void shouldThrowExceptionOnCreate_whenChapterDoesNotExist() {
        final var creationDto = aTaskCreationDto();

        doThrowChapterDoesNotExist(creationDto.chapterId());

        assertThatThrownBy(() -> service.create(creationDto))
                .isInstanceOf(ChapterDoesNotExistException.class)
                .hasMessageContaining(String.valueOf(creationDto.chapterId().id().toString()));

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void shouldUpdate_whenTaskExists() {
        final var task = aTask();
        final var updateDto = aTaskUpdateDto();

        when(taskLoader.getTaskIfExists(task.getId())).thenReturn(task);

        when(taskRepository.save(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        final var updated = service.update(task.getId(), updateDto);

        verify(taskRepository).save(task);

        assertEquals(updated.getId(), task.getId());
        assertEquals(updated.getEstimatedMinutes(), updateDto.estimatedMinutes());
        assertEquals(updated.getCompletedMinutes(), updateDto.completedMinutes());
        assertEquals(updated.getScheduledAt(), updateDto.scheduledAt());
        assertEquals(updated.getChapter(), task.getChapter());
    }

    @Test
    void shouldThrowExceptionOnUpdate_whenTaskDoesNotExists() {
        final var task = aTask();
        final var updateDto = aTaskUpdateDto();

        when(taskLoader.getTaskIfExists(eq(task.getId())))
                .thenThrow((new TaskDoesNotExistException(task.getId())));

        assertThatThrownBy(() -> service.update(task.getId(), updateDto))
                .isInstanceOf(TaskDoesNotExistException.class)
                .hasMessageContaining(String.valueOf(task.getId().id()));

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void shouldDeleteTask_whenTaskExists() {
        final var task = aTask();

        when(taskLoader.getTaskIfExists(task.getId())).thenReturn(task);

        service.delete(task.getId());

        verify(taskRepository).delete(task);
    }

    @Test
    void shouldThrowExceptionOnDelete_whenTaskDoesNotExists() {
        final var task = aTask();

        when(taskLoader.getTaskIfExists(eq(task.getId())))
                .thenThrow((new TaskDoesNotExistException(task.getId())));

        assertThatThrownBy(() -> service.delete(task.getId()))
                .isInstanceOf(TaskDoesNotExistException.class)
                .hasMessageContaining(String.valueOf(task.getId().id()));

        verify(taskRepository, never()).delete(any(Task.class));
    }

    @Test
    void shouldDeleteAllTasks_whenChapterExists() {
        final var chapter = aChapter();
        final var task = aTaskWithChapterId(chapter.getId());

        when(taskRepository.findAllByChapter(chapter.getId())).thenReturn(List.of(task));

        service.deleteAllForChapter(chapter.getId());

        verify(taskRepository).deleteAll(List.of(task));
    }

    @Test
    void shouldThrowExceptionOnDeleteAllTasks_whenChapterDoesNotExists() {
        final var chapter = aChapter();

        doThrowChapterDoesNotExist(chapter.getId());

        assertThatThrownBy(() -> service.deleteAllForChapter(chapter.getId()))
                .isInstanceOf(ChapterDoesNotExistException.class)
                .hasMessageContaining(String.valueOf(chapter.getId().id().toString()));

        verify(taskRepository, never()).save(any(Task.class));
    }

    @Test
    void shouldGetAllTasksForChapter_whenChapterExists() {
        final var chapter = aChapter();

        service.findAllForChapter(chapter.getId());

        verify(taskRepository).findAllByChapter(chapter.getId());
    }

    @Test
    void shouldThrowExceptionOnGetAllTasksForChapter_whenChapterDoesNotExists() {
        final var chapter = aChapter();

        doThrowChapterDoesNotExist(chapter.getId());

        assertThatThrownBy(() -> service.findAllForChapter(chapter.getId()))
                .isInstanceOf(ChapterDoesNotExistException.class)
                .hasMessageContaining(String.valueOf(chapter.getId().id().toString()));

        verify(taskRepository, never()).findAllByChapter(chapter.getId());
    }

    @Test
    void shouldCompleteTask_whenTaskExistsAndNotDone() {
        final var task = aTask();
        final var dto = aTaskCompleteDto();

        when(taskLoader.getTaskIfExists(task.getId())).thenReturn(task);
        when(taskRepository.save(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        service.completeTask(task.getId(), dto);

        final var captor = ArgumentCaptor.forClass(Task.class);
        verify(taskRepository).save(captor.capture());

        final var savedTask = captor.getValue();

        assertEquals(TaskStatus.DONE, savedTask.getStatus());
        assertEquals(dto.completedMinutes(), savedTask.getCompletedMinutes());

        verify(chapterRecalculationService).recalculateCompletedMinutes(task.getChapter());
    }

    @Test
    void shouldThrowExceptionOnComplete_whenTaskExistsAndIsDone() {
        final var task = aTaskWithStatus(TaskStatus.DONE);
        final var dto = aTaskCompleteDto();

        when(taskLoader.getTaskIfExists(task.getId())).thenReturn(task);

        assertThatThrownBy(() -> service.completeTask(task.getId(), dto))
                .isInstanceOf(TaskAlreadyDoneException.class)
                .hasMessageContaining(String.valueOf(task.getId().id()));

        verify(taskRepository, never()).save(any());
        verify(chapterRecalculationService, never()).recalculateCompletedMinutes(any());
    }

    @ParameterizedTest
    @MethodSource("provideTaskCompleteDtos")
    void shouldThrowExceptionOnComplete_whenDtoDataIsInvalid(final TaskCompleteDto dto) {
        final var task = aTask();

        when(taskLoader.getTaskIfExists(task.getId())).thenReturn(task);

        assertThatThrownBy(() -> service.completeTask(task.getId(), dto))
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

        when(arcLoader.getActiveArcForUser(userId)).thenReturn(arc);
        when(chapterLoader.findByDate(eq(arc.getId()), any())).thenReturn(chapter);
        when(taskLoader.getTasksForChapter(chapter.getId())).thenReturn(tasks);

        final var result = service.getTodaysTasks(userId);

        assertEquals(tasks, result);

        verify(userLoader).assertUserExists(userId);
        verify(arcLoader).getActiveArcForUser(userId);
        verify(chapterLoader).findByDate(eq(arc.getId()), any(LocalDate.class));
        verify(taskLoader).getTasksForChapter(chapter.getId());
    }

    @Test
    void shouldThrowErrorOnTodayTasks_whenArcIsValidAndChapterDoesNotExists() {
        final var userId = UserId.random();
        final var arc = anArc();

        when(arcLoader.getActiveArcForUser(userId)).thenReturn(arc);

        when(chapterLoader.findByDate(eq(arc.getId()), any(LocalDate.class)))
                .thenThrow(new NoChapterForArcException(arc.getId(), LocalDate.now()));

        assertThatThrownBy(() -> service.getTodaysTasks(userId))
                .isInstanceOf(NoChapterForArcException.class)
                .hasMessageContaining(String.valueOf(arc.getId().id()));

        verify(userLoader).assertUserExists(userId);
        verify(arcLoader).getActiveArcForUser(userId);
        verify(chapterLoader).findByDate(eq(arc.getId()), any(LocalDate.class));
        verify(taskLoader, never()).getTasksForChapter(any(ChapterId.class));
    }

    @Test
    void shouldThrowErrorOnTodayTasks_whenArcIsInValid() {
        final var userId = UserId.random();

        when(arcLoader.getActiveArcForUser(userId))
                .thenThrow(new NoActiveArcException(userId));

        assertThatThrownBy(() -> service.getTodaysTasks(userId))
                .isInstanceOf(NoActiveArcException.class)
                .hasMessageContaining(String.valueOf(userId.id()));

        verify(userLoader).assertUserExists(userId);
        verify(arcLoader).getActiveArcForUser(userId);
        verify(chapterLoader, never()).findByDate(any(ArcId.class), any(LocalDate.class));
        verify(taskLoader, never()).getTasksForChapter(any(ChapterId.class));
    }

    private static Stream<Arguments> provideTaskCompleteDtos() {
        return Stream.of(
                Arguments.of(aTaskCompleteDtoWithMinutes(-10)),
                Arguments.of(aTaskCompleteDtoWithMinutes(MINUTES_PER_DAY + 1))
            );
    }

    private void doThrowChapterDoesNotExist(final ChapterId chapterId) {
        doThrow(new ChapterDoesNotExistException(chapterId))
                .when(chapterLoader)
                .assertChapterExists(eq(chapterId));
    }
}