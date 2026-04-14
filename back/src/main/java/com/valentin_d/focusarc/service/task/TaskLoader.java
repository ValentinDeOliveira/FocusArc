package com.valentin_d.focusarc.service.task;

import com.valentin_d.focusarc.dto.tag.TagTaskStatsDto;
import com.valentin_d.focusarc.exception.task.TaskDoesNotExistException;
import com.valentin_d.focusarc.exception.task.TaskInProgressException;
import com.valentin_d.focusarc.model.id.ChapterId;
import com.valentin_d.focusarc.model.id.TaskId;
import com.valentin_d.focusarc.model.task.Task;
import com.valentin_d.focusarc.model.task.TaskStatus;
import com.valentin_d.focusarc.repository.TaskRepository;
import com.valentin_d.focusarc.service.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import static com.valentin_d.focusarc.model.task.TaskStatus.PENDING;
import static java.util.stream.Collectors.groupingBy;

@Component
@RequiredArgsConstructor
public class TaskLoader extends BaseService {
    private final TaskRepository taskRepository;

    public Task getTaskIfExists(TaskId taskId) {
        return fetchOrThrow(taskRepository, taskId, () -> new TaskDoesNotExistException(taskId));
    }

    public List<Task> getTasksForChapter(ChapterId chapterId) {
        return taskRepository.findAllByChapterOrderByStartAtAsc(chapterId);
    }

    public List<Task> getNotCompletedTaskForChapter(ChapterId chapterId) {
        return taskRepository.findAllByChapterAndStatusIn(chapterId, PENDING);
    }

    public Optional<Task> getTask(TaskId taskId) {
        return taskRepository.findById(taskId);
    }

    public boolean existForChapterAtTime(ChapterId chapterId, Instant start, Instant end) {
        return taskRepository.existsByChapterAndStatusInAndStartAtBeforeAndEndAtAfter(chapterId,
                PENDING, end, start);
    }

    public boolean existForChapterAtTimeExcluding(ChapterId chapterId, TaskId excludedId,
                                                  Instant start, Instant end) {
        return taskRepository.existsByChapterAndStatusInAndIdNotAndStartAtBeforeAndEndAtAfter(
                chapterId, PENDING, excludedId, end, start);
    }

    public void assertNoOtherTasksInProgressForChapter(ChapterId chapterId, TaskId taskId) {
        if(taskRepository.existsByChapterAndStatus(chapterId, TaskStatus.IN_PROGRESS)) {
            throw new TaskInProgressException(chapterId, taskId);
        }
    }

    public List<TagTaskStatsDto> getNumberTasksPerTagForChapters(List<ChapterId> chapterIds) {
        return taskRepository
                .findAllByChapterIn(chapterIds)
                .stream()
                .filter(task -> task.getId() != null)
                .collect(groupingBy(Task::getTagId))
                .entrySet()
                .stream()
                .map(e -> new TagTaskStatsDto(
                        e.getKey(),
                        (long) e.getValue().size(),
                        e.getValue().stream().filter(Task::isDone).count()
                ))
                .toList();
    }
}