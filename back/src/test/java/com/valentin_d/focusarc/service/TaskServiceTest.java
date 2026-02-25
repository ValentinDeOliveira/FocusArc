package com.valentin_d.focusarc.service;

import com.valentin_d.focusarc.dto.task.TaskCompleteDto;
import com.valentin_d.focusarc.exception.task.TaskAlreadyDoneException;
import com.valentin_d.focusarc.exception.task.TaskDoesNotExistException;
import com.valentin_d.focusarc.exception.task.TaskInvalidMinuteException;
import com.valentin_d.focusarc.model.task.Task;
import com.valentin_d.focusarc.model.task.TaskStatus;
import com.valentin_d.focusarc.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.valentin_d.focusarc.fixtures.factory.ChapterFactory.aChapter;
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
    private ChapterService chapterService;
    @Mock
    private ChapterRecalculationService chapterRecalculationService;
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
    void shouldUpdate_whenTaskExists() {
        final var task = aTask();
        final var updateDto = aTaskUpdateDto();

        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));

        when(taskRepository.save(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        final var updated = service.update(task.getId(), updateDto);

        verify(taskRepository).save(task);
        verify(taskRepository).findById(task.getId());

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

        when(taskRepository.findById(eq(task.getId()))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(task.getId(), updateDto))
                .isInstanceOf(TaskDoesNotExistException.class)
                .hasMessageContaining(String.valueOf(task.getId().id()));

        verify(taskRepository, never()).save(any(Task.class));
        verify(taskRepository).findById(task.getId());
    }

    @Test
    void shouldDeleteTask_whenTaskExists() {
        final var task = aTask();

        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));

        service.delete(task.getId());

        verify(taskRepository).findById(task.getId());
        verify(taskRepository).delete(task);
    }

    @Test
    void shouldThrowExceptionOnDelete_whenTaskDoesNotExists() {
        final var task = aTask();

        when(taskRepository.findById(task.getId())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.delete(task.getId()))
                .isInstanceOf(TaskDoesNotExistException.class)
                .hasMessageContaining(String.valueOf(task.getId().id()));

        verify(taskRepository, never()).delete(any(Task.class));
        verify(taskRepository).findById(task.getId());
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
    void shouldGetAllTasksForChapter_whenChapterExists() {
        final var chapter = aChapter();

        service.findAllForChapter(chapter.getId());

        verify(taskRepository).findAllByChapter(chapter.getId());
    }

    @Test
    void shouldCompleteTask_whenTaskExistsAndNotDone() {
        final var task = aTask();
        final var dto = aTaskCompleteDto();

        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));
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

        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));

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

        when(taskRepository.findById(task.getId())).thenReturn(Optional.of(task));

        assertThatThrownBy(() -> service.completeTask(task.getId(), dto))
                .isInstanceOf(TaskInvalidMinuteException.class)
                .hasMessageContaining(String.valueOf(task.getId().id()))
                .hasMessageContaining(String.valueOf(dto.completedMinutes()));

        verify(taskRepository, never()).save(any());
        verify(chapterRecalculationService, never()).recalculateCompletedMinutes(any());
    }

    private static Stream<Arguments> provideTaskCompleteDtos() {
        return Stream.of(
                Arguments.of(aTaskCompleteDtoWithMinutes(-10)),
                Arguments.of(aTaskCompleteDtoWithMinutes(MINUTES_PER_DAY + 1))
            );
    }
}