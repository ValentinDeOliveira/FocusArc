package com.valentin_d.focusarc.service;

import com.valentin_d.focusarc.dto.task.TaskCompleteDto;
import com.valentin_d.focusarc.dto.task.TaskCreationDto;
import com.valentin_d.focusarc.dto.task.TaskUpdateDto;
import com.valentin_d.focusarc.exception.ChapterDoesNotExistException;
import com.valentin_d.focusarc.exception.task.TaskAlreadyDoneException;
import com.valentin_d.focusarc.exception.task.TaskDoesNotExistException;
import com.valentin_d.focusarc.exception.task.TaskInvalidMinuteException;
import com.valentin_d.focusarc.model.id.ChapterId;
import com.valentin_d.focusarc.model.id.TaskId;
import com.valentin_d.focusarc.model.task.Task;
import com.valentin_d.focusarc.model.task.TaskStatus;
import com.valentin_d.focusarc.repository.ChapterRepository;
import com.valentin_d.focusarc.repository.TaskRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.valentin_d.focusarc.shared.TimeConstraints.MINUTES_PER_DAY;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final ChapterRepository chapterRepository;
    private final ChapterService chapterService;

    public Optional<Task> findById(final TaskId taskId) {
        return taskRepository.findById(taskId);
    }

    public List<Task> findAllForChapter(final ChapterId chapterId) {
        assertChapterExists(chapterId);

        return taskRepository.findAllByChapter(chapterId);
    }

    public Task create(@NotNull final TaskCreationDto taskCreationDto) {
        assertChapterExists(taskCreationDto.chapterId());

        assertMinutes(taskCreationDto.estimatedMinutes());

        final var task = new Task(taskCreationDto.chapterId(),
                taskCreationDto.estimatedMinutes(), taskCreationDto.scheduledAt());

        final var savedTask = taskRepository.save(task);
        chapterService.recalculateEstimatedMinutes(taskCreationDto.chapterId());

        return savedTask;
    }

    public Task update(@NotNull final TaskId taskId, @NotNull final TaskUpdateDto chapterUpdateDto) {
        final var task = getTaskIfExists(taskId);

        if (chapterUpdateDto.completedMinutes() != null) {
            assertMinutes(taskId, chapterUpdateDto.completedMinutes());
            task.setCompletedMinutes(chapterUpdateDto.completedMinutes());
        }
        if (chapterUpdateDto.estimatedMinutes() != null) {
            assertMinutes(taskId, chapterUpdateDto.estimatedMinutes());
            task.setEstimatedMinutes(chapterUpdateDto.estimatedMinutes());
        }
        if (chapterUpdateDto.scheduledAt() != null) task.setScheduledAt(chapterUpdateDto.scheduledAt());
        if (chapterUpdateDto.taskStatus() != null) task.setStatus(chapterUpdateDto.taskStatus());

        return taskRepository.save(task);
    }

    public void delete(@NotNull final TaskId taskId) {
        final var task = getTaskIfExists(taskId);
        taskRepository.delete(task);
    }

    public void deleteAllForChapter(@NotNull final ChapterId chapterId) {
        assertChapterExists(chapterId);

        final var chapters = taskRepository.findAllByChapter(chapterId);
        taskRepository.deleteAll(chapters);
    }

    public void completeTask(@NotNull final TaskId taskId, @NotNull final TaskCompleteDto taskCompleteDto) {
        final var task = getTaskIfExists(taskId);

        if (task.isDone()) {
            throw new TaskAlreadyDoneException(taskId);
        }
        assertMinutes(taskId, taskCompleteDto.completedMinutes());

        task.setStatus(TaskStatus.DONE);
        task.setCompletedMinutes(taskCompleteDto.completedMinutes());
        taskRepository.save(task);
        chapterService.recalculateCompletedMinutes(task.getChapter());
    }

    private void assertChapterExists(final ChapterId chapterId) {
        if (!chapterRepository.existsById(chapterId)) {
            throw new ChapterDoesNotExistException(chapterId);
        }
    }

    private void assertMinutes(final int minutes) {
        if (minutes < 0 || minutes > MINUTES_PER_DAY) {
            throw new TaskInvalidMinuteException(minutes);
        }
    }

    private void assertMinutes(final TaskId taskId, final int minutes) {
        if (minutes < 0 || minutes > MINUTES_PER_DAY) {
            throw new TaskInvalidMinuteException(taskId, minutes);
        }
    }

    private Task getTaskIfExists(final TaskId taskId) {
        return findById(taskId).orElseThrow(() -> new TaskDoesNotExistException(taskId));
    }
}