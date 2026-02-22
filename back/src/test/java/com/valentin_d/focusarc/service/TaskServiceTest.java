package com.valentin_d.focusarc.service;

import com.valentin_d.focusarc.exception.ChapterDoesNotExistException;
import com.valentin_d.focusarc.exception.TaskDoesNotExistException;
import com.valentin_d.focusarc.model.id.ChapterId;
import com.valentin_d.focusarc.model.task.Task;
import com.valentin_d.focusarc.repository.ChapterRepository;
import com.valentin_d.focusarc.repository.TaskRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static com.valentin_d.focusarc.fixtures.factory.ChapterFactory.aChapter;
import static com.valentin_d.focusarc.fixtures.factory.TaskFactory.*;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;
    @Mock
    private ChapterRepository chapterRepository;

    @InjectMocks
    private TaskService service;

    @Test
    void shouldCreateTask_whenChapterExist() {
        final var creationDto = aTaskCreationDto();
        when(chapterRepository.existsById(any())).thenReturn(true);

        when(taskRepository.save(any(Task.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        final var result = service.create(creationDto);

        assertEquals(creationDto.chapterId(), result.getChapter());
        assertEquals(creationDto.estimatedMinutes(), result.getEstimatedMinutes());
        assertEquals(0, result.getCompletedMinutes());
        assertEquals(creationDto.scheduledAt(), result.getScheduledAt());

        verify(chapterRepository).existsById(any(ChapterId.class));
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void shouldThrowExceptionOnCreation_whenChapterNotFound() {
        final var creationDto = aTaskCreationDto();

        when(chapterRepository.existsById(any())).thenReturn(false);

        assertThatThrownBy(() -> service.create(creationDto))
                .isInstanceOf(ChapterDoesNotExistException.class)
                .hasMessageContaining(String.valueOf(creationDto.chapterId().id().toString()));

        verify(chapterRepository).existsById(creationDto.chapterId());
        verify(taskRepository, never()).save(any(Task.class));
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

        when(chapterRepository.existsById(chapter.getId())).thenReturn(true);
        when(taskRepository.findAllByChapter(chapter.getId())).thenReturn(List.of(task));

        service.deleteAllForChapter(chapter.getId());

        verify(chapterRepository).existsById(chapter.getId());
        verify(taskRepository).deleteAll(List.of(task));
    }

    @Test
    void shouldThrowExceptionOnDeleteAllTask_whenChapterDoesNotExists() {
        final var chapter = aChapter();

        when(chapterRepository.existsById(chapter.getId())).thenReturn(false);

        assertThatThrownBy(() -> service.deleteAllForChapter(chapter.getId()))
                .isInstanceOf(ChapterDoesNotExistException.class)
                .hasMessageContaining(String.valueOf(chapter.getId().id()));

        verify(taskRepository, never()).deleteAll(anyList());
        verify(chapterRepository).existsById(chapter.getId());
    }

    @Test
    void shouldGetAllTasksForChapter_whenChapterExists() {
        final var chapter = aChapter();

        when(chapterRepository.existsById(chapter.getId())).thenReturn(true);

        service.findAllForChapter(chapter.getId());

        verify(chapterRepository).existsById(chapter.getId());
        verify(taskRepository).findAllByChapter(chapter.getId());
    }

    @Test
    void shouldThrowExceptionOnGetAllTaskForChapter_whenChapterDoesNotExists() {
        final var chapter = aChapter();

        when(chapterRepository.existsById(chapter.getId())).thenReturn(false);

        assertThatThrownBy(() -> service.findAllForChapter(chapter.getId()))
                .isInstanceOf(ChapterDoesNotExistException.class)
                .hasMessageContaining(String.valueOf(chapter.getId().id()));

        verify(chapterRepository).existsById(chapter.getId());
        verify(taskRepository, never()).findAllByChapter(chapter.getId());
    }
}