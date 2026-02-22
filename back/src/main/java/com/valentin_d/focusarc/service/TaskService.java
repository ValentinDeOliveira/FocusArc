package com.valentin_d.focusarc.service;

import com.valentin_d.focusarc.dto.task.TaskCreationDto;
import com.valentin_d.focusarc.dto.task.TaskUpdateDto;
import com.valentin_d.focusarc.exception.ChapterDoesNotExistException;
import com.valentin_d.focusarc.exception.TaskDoesNotExistException;
import com.valentin_d.focusarc.model.id.ChapterId;
import com.valentin_d.focusarc.model.id.TaskId;
import com.valentin_d.focusarc.model.task.Task;
import com.valentin_d.focusarc.repository.ChapterRepository;
import com.valentin_d.focusarc.repository.TaskRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final ChapterRepository chapterRepository;

    public Optional<Task> findById(final TaskId taskId) {
        return taskRepository.findById(taskId);
    }

    public List<Task> findAllForChapter(final ChapterId chapterId) {
        assertChapterExists(chapterId);

        return taskRepository.findAllByChapter(chapterId);
    }

    public Task create(@NotNull final TaskCreationDto taskCreationDto) {
        assertChapterExists(taskCreationDto.chapterId());

        final var chapter = new Task(taskCreationDto.chapterId(),
                taskCreationDto.estimatedMinutes(), taskCreationDto.scheduledAt());
        return taskRepository.save(chapter);
    }

    public Task update(@NotNull final TaskId taskId, @NotNull final TaskUpdateDto chapterUpdateDto) {
        final var task = findById(taskId).orElseThrow(() -> new TaskDoesNotExistException(taskId));

        if (chapterUpdateDto.completedMinutes() != null) task.setCompletedMinutes(chapterUpdateDto.completedMinutes());
        if (chapterUpdateDto.estimatedMinutes() != null) task.setEstimatedMinutes(chapterUpdateDto.estimatedMinutes());
        if (chapterUpdateDto.scheduledAt() != null) task.setScheduledAt(chapterUpdateDto.scheduledAt());

        return taskRepository.save(task);
    }

    public void delete(@NotNull final TaskId taskId) {
        final var task = findById(taskId).orElseThrow(() -> new TaskDoesNotExistException(taskId));
        taskRepository.delete(task);
    }

    public void deleteAllForChapter(@NotNull final ChapterId chapterId) {
        assertChapterExists(chapterId);

        final var chapters = taskRepository.findAllByChapter(chapterId);
        taskRepository.deleteAll(chapters);
    }

    private void assertChapterExists(final ChapterId chapterId) {
        if (!chapterRepository.existsById(chapterId)) {
            throw new ChapterDoesNotExistException(chapterId);
        }
    }
}