package com.valentin_d.focusarc.service.task;

import com.valentin_d.focusarc.dto.task.TaskCompleteDto;
import com.valentin_d.focusarc.dto.task.TaskCreationDto;
import com.valentin_d.focusarc.dto.task.TaskUpdateDto;
import com.valentin_d.focusarc.exception.task.TaskAlreadyDoneException;
import com.valentin_d.focusarc.exception.task.TaskInvalidMinuteException;
import com.valentin_d.focusarc.model.id.ChapterId;
import com.valentin_d.focusarc.model.id.TaskId;
import com.valentin_d.focusarc.model.id.UserId;
import com.valentin_d.focusarc.model.task.Task;
import com.valentin_d.focusarc.model.task.TaskStatus;
import com.valentin_d.focusarc.repository.TaskRepository;
import com.valentin_d.focusarc.service.ContextLoader;
import com.valentin_d.focusarc.service.chapter.ChapterLoader;
import com.valentin_d.focusarc.service.chapter.ChapterRecalculationService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Optional;

import static com.valentin_d.focusarc.shared.TimeConstraints.MINUTES_PER_DAY;

@Service
@Validated
@RequiredArgsConstructor
public class TaskService {
    private final TaskRepository taskRepository;
    private final ChapterRecalculationService chapterRecalculationService;
    private final TaskLoader taskLoader;
    private final ChapterLoader chapterLoader;
    private final ContextLoader contextLoader;

    public Optional<Task> findById(final TaskId taskId) {
        return taskRepository.findById(taskId);
    }

    public List<Task> findAllForChapter(final ChapterId chapterId) {
        chapterLoader.assertChapterExists(chapterId);

        return taskRepository.findAllByChapter(chapterId);
    }

    public Task create(@NotNull final TaskCreationDto taskCreationDto) {
        chapterLoader.assertChapterExists(taskCreationDto.chapterId());

        assertMinutes(taskCreationDto.estimatedMinutes());

        final var task = new Task(taskCreationDto.chapterId(),
                taskCreationDto.estimatedMinutes(), taskCreationDto.scheduledAt());

        final var savedTask = taskRepository.save(task);
        chapterRecalculationService.recalculateEstimatedMinutes(taskCreationDto.chapterId());

        return savedTask;
    }

    public Task update(@NotNull final TaskId taskId, @NotNull final TaskUpdateDto taskUpdateDto) {
        final var task = taskLoader.getTaskIfExists(taskId);
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
        final var task = taskLoader.getTaskIfExists(taskId);
        taskRepository.delete(task);

        chapterRecalculationService.recalculateEstimatedMinutes(task.getChapter());
        chapterRecalculationService.recalculateCompletedMinutes(task.getChapter());
    }

    public void deleteAllForChapter(@NotNull final ChapterId chapterId) {
        chapterLoader.assertChapterExists(chapterId);

        final var tasks = taskRepository.findAllByChapter(chapterId);
        taskRepository.deleteAll(tasks);

        chapterRecalculationService.recalculateEstimatedMinutes(chapterId);
        chapterRecalculationService.recalculateCompletedMinutes(chapterId);
    }

    public void completeTask(@NotNull final TaskId taskId, @NotNull final TaskCompleteDto taskCompleteDto) {
        final var task = taskLoader.getTaskIfExists(taskId);

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
        final var chapter = contextLoader.getChapterFromUserId(userId);

        return taskLoader.getTasksForChapter(chapter.getId());
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