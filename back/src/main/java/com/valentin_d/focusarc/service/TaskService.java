package com.valentin_d.focusarc.service;

import com.valentin_d.focusarc.dto.task.TaskCompleteDto;
import com.valentin_d.focusarc.dto.task.TaskCreationDto;
import com.valentin_d.focusarc.dto.task.TaskUpdateDto;
import com.valentin_d.focusarc.exception.task.TaskAlreadyDoneException;
import com.valentin_d.focusarc.exception.task.TaskDoesNotExistException;
import com.valentin_d.focusarc.exception.task.TaskInvalidMinuteException;
import com.valentin_d.focusarc.model.id.ChapterId;
import com.valentin_d.focusarc.model.id.TaskId;
import com.valentin_d.focusarc.model.id.UserId;
import com.valentin_d.focusarc.model.task.Task;
import com.valentin_d.focusarc.model.task.TaskStatus;
import com.valentin_d.focusarc.repository.TaskRepository;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import static com.valentin_d.focusarc.shared.TimeConstraints.MINUTES_PER_DAY;

@Service
@RequiredArgsConstructor
public class TaskService extends BaseService {
    private final TaskRepository taskRepository;
    private final ChapterService chapterService;
    private final ChapterRecalculationService chapterRecalculationService;

    public Optional<Task> findById(final TaskId taskId) {
        return taskRepository.findById(taskId);
    }

    public List<Task> findAllForChapter(final ChapterId chapterId) {
        chapterService.assertChapterExists(chapterId);

        return taskRepository.findAllByChapter(chapterId);
    }

    public Task create(@NotNull final TaskCreationDto taskCreationDto) {
        chapterService.assertChapterExists(taskCreationDto.chapterId());

        assertMinutes(taskCreationDto.estimatedMinutes());

        final var task = new Task(taskCreationDto.chapterId(),
                taskCreationDto.estimatedMinutes(), taskCreationDto.scheduledAt());

        final var savedTask = taskRepository.save(task);
        chapterRecalculationService.recalculateEstimatedMinutes(taskCreationDto.chapterId());

        return savedTask;
    }

    public Task update(@NotNull final TaskId taskId, @NotNull final TaskUpdateDto taskUpdateDto) {
        final var task = getTaskIfExists(taskId);
        final var beforeUpdateTask = task.snapshot();

        updateTask(task, taskUpdateDto);

        final var savedTask = taskRepository.save(task);

        if (beforeUpdateTask.isEstimatedMinutesChanged(savedTask)) {
            chapterRecalculationService.recalculateEstimatedMinutes(task.getChapter());
        }
        if (beforeUpdateTask.isCompletedMinutesChanged(savedTask)) {
            chapterRecalculationService.recalculateCompletedMinutes(task.getChapter());
        }

        return savedTask;
    }

    public void delete(@NotNull final TaskId taskId) {
        final var task = getTaskIfExists(taskId);
        taskRepository.delete(task);
    }

    public void deleteAllForChapter(@NotNull final ChapterId chapterId) {
        chapterService.assertChapterExists(chapterId);

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
        chapterRecalculationService.recalculateCompletedMinutes(task.getChapter());
    }

    public List<Task> getTodaysTasks(@NotNull final UserId userId) {
        return null;
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
        return fetchOrThrow(taskRepository, taskId, () -> new TaskDoesNotExistException(taskId));
    }

    private void updateTask(final Task task, final TaskUpdateDto dto) {
        if (dto.completedMinutes() != null) {
            assertMinutes(task.getId(), dto.completedMinutes());
            task.setCompletedMinutes(dto.completedMinutes());
        }
        if (dto.estimatedMinutes() != null) {
            assertMinutes(task.getId(), dto.estimatedMinutes());
            task.setEstimatedMinutes(dto.estimatedMinutes());
        }
        if (dto.scheduledAt() != null) task.setScheduledAt(dto.scheduledAt());
        if (dto.taskStatus() != null) task.setStatus(dto.taskStatus());
    }
}