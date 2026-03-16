package com.valentin_d.focusarc.service.task;

import com.valentin_d.focusarc.dto.task.TaskCompleteDto;
import com.valentin_d.focusarc.dto.task.TaskCreationDto;
import com.valentin_d.focusarc.dto.task.TaskUpdateDto;
import com.valentin_d.focusarc.exception.task.TaskAlreadyDoneException;
import com.valentin_d.focusarc.exception.task.TaskInvalidMinuteException;
import com.valentin_d.focusarc.exception.task.TaskOverlapException;
import com.valentin_d.focusarc.model.id.ChapterId;
import com.valentin_d.focusarc.model.id.TaskId;
import com.valentin_d.focusarc.model.id.UserId;
import com.valentin_d.focusarc.model.task.Task;
import com.valentin_d.focusarc.model.task.TaskStatus;
import com.valentin_d.focusarc.repository.TaskRepository;
import com.valentin_d.focusarc.service.ContextLoader;
import com.valentin_d.focusarc.service.chapter.ChapterRecalculationService;
import com.valentin_d.focusarc.service.tag.TagLoader;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
    private final TagLoader tagLoader;
    private final ContextLoader contextLoader;

    public Optional<Task> findById(@NotNull TaskId taskId,
                                   @NotNull UserId userId) {
        final var optTask = taskLoader.getTask(taskId);

        if (optTask.isEmpty()) {
            return Optional.empty();
        }

        final var task = optTask.get();
        contextLoader.assertChapterForUser(task.getChapter(), userId);

        return Optional.of(task);
    }

    public List<Task> findAllForChapter(@NotNull ChapterId chapterId,
                                        @NotNull UserId userId) {
        contextLoader.assertChapterForUser(chapterId, userId);

        return taskRepository.findAllByChapter(chapterId);
    }

    public Task create(@NotNull TaskCreationDto dto,
                       @NotNull UserId userId) {
        contextLoader.assertChapterForUser(dto.chapterId(), userId);

        tagLoader.assertTagsForUser(userId, dto.tag());
        assertMinutes(dto.estimatedMinutes());
        assertNotOverlapping(dto.chapterId(), dto.estimatedMinutes(), dto.scheduledAt());

        final var task = new Task(
                dto.chapterId(),
                dto.estimatedMinutes(),
                dto.scheduledAt(),
                dto.name(),
                dto.description(),
                dto.tag()
        );

        final var savedTask = taskRepository.save(task);
        chapterRecalculationService.recalculateEstimatedMinutes(dto.chapterId());

        return savedTask;
    }

    public Task update(@NotNull TaskId taskId,
                       @NotNull TaskUpdateDto taskUpdateDto,
                       @NotNull UserId userId) {
        final var task = taskLoader.getTaskIfExists(taskId);
        tagLoader.assertTagsForUser(userId, taskUpdateDto.tag());
        contextLoader.assertChapterForUser(task.getChapter(), userId);
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

    public void delete(@NotNull TaskId taskId,
                       @NotNull UserId userId) {
        final var task = taskLoader.getTaskIfExists(taskId);
        contextLoader.assertChapterForUser(task.getChapter(), userId);

        taskRepository.delete(task);

        chapterRecalculationService.recalculateEstimatedMinutes(task.getChapter());
        chapterRecalculationService.recalculateCompletedMinutes(task.getChapter());
    }

    public void deleteAllForChapter(@NotNull ChapterId chapterId,
                                    @NotNull UserId userId) {
        contextLoader.assertChapterForUser(chapterId, userId);
        deleteTasksForChapter(chapterId);
    }

    public void deleteTasksForChapter(@NotNull ChapterId chapterId) {
        final var tasks = taskRepository.findAllByChapter(chapterId);
        taskRepository.deleteAll(tasks);

        chapterRecalculationService.recalculateEstimatedMinutes(chapterId);
        chapterRecalculationService.recalculateCompletedMinutes(chapterId);
    }

    public Task completeTask(@NotNull TaskId taskId,
                             @NotNull UserId userId,
                             @NotNull TaskCompleteDto taskCompleteDto) {
        final var task = taskLoader.getTaskIfExists(taskId);
        contextLoader.assertChapterForUser(task.getChapter(), userId);

        if (task.isDone()) {
            throw new TaskAlreadyDoneException(taskId);
        }

        assertMinutes(taskId, taskCompleteDto.completedMinutes());

        task.setStatus(TaskStatus.DONE);
        task.setCompletedMinutes(taskCompleteDto.completedMinutes());
        taskRepository.save(task);
        chapterRecalculationService.recalculateCompletedMinutes(task.getChapter());

        return task;
    }

    public List<Task> getTodaysTasks(@NotNull UserId userId) {
        final var chapter = contextLoader.getChapterFromUserId(userId);

        return taskLoader.getTasksForChapter(chapter.getId());
    }

    private void assertMinutes(int minutes) {
        if (minutes < 0 || minutes > MINUTES_PER_DAY) {
            throw new TaskInvalidMinuteException(minutes);
        }
    }

    private void assertMinutes(TaskId taskId, int minutes) {
        if (minutes < 0 || minutes > MINUTES_PER_DAY) {
            throw new TaskInvalidMinuteException(taskId, minutes);
        }
    }

    private void updateTask(Task task, TaskUpdateDto dto) {
        if (dto.scheduledAt() != null || dto.estimatedMinutes() != null) {
            // use dto value if provided, otherwise fall back to current task value
            final var newStart = dto.scheduledAt() != null
                    ? dto.scheduledAt() : task.getStartAt();
            final var newEstimated = dto.estimatedMinutes() != null
                    ? dto.estimatedMinutes() : task.getEstimatedMinutes();

            assertNotOverlapping(task.getChapter(), newEstimated, newStart, task.getId());
            task.updateSchedule(newStart, newEstimated);
        }

        if (dto.completedMinutes() != null) {
            assertMinutes(task.getId(), dto.completedMinutes());
            task.setCompletedMinutes(dto.completedMinutes());
        }
        if (dto.estimatedMinutes() != null) {
            assertMinutes(task.getId(), dto.estimatedMinutes());
            task.setEstimatedMinutes(dto.estimatedMinutes());
        }

        if (dto.scheduledAt() != null) task.setStartAt(dto.scheduledAt());
        if (dto.taskStatus() != null) task.setStatus(dto.taskStatus());
        // TODO: assert name not empty
        if (dto.name() != null) task.setName(dto.name());
        if (dto.description() != null) task.setDescription(dto.description());
        // how to differenciate "no tag" and remove tag
        if (dto.tag() != null) task.setTag(dto.tag());
    }


    private void assertNotOverlapping(@NotNull ChapterId chapterId,
                                      @Positive @Max(MINUTES_PER_DAY) int estimatedMinutes,
                                      @FutureOrPresent Instant scheduledAt) {
        final var estimatedEnd = scheduledAt.plus(estimatedMinutes, ChronoUnit.MINUTES);
        if (taskLoader.existForChapterAtTime(chapterId, scheduledAt, estimatedEnd)) {
            throw new TaskOverlapException(chapterId, scheduledAt, estimatedEnd);
        }
    }


    private void assertNotOverlapping(@NotNull ChapterId chapterId,
                                      @Positive @Max(MINUTES_PER_DAY) int estimatedMinutes,
                                      @FutureOrPresent Instant scheduledAt,
                                      @NotNull TaskId taskId) {
        final var estimatedEnd = scheduledAt.plus(estimatedMinutes, ChronoUnit.MINUTES);
        if (taskLoader.existForChapterAtTimeExcluding(chapterId, taskId, scheduledAt, estimatedEnd)) {
            throw new TaskOverlapException(chapterId, scheduledAt, estimatedEnd);
        }
    }
}