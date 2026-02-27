package com.valentin_d.focusarc.service.task;

import com.valentin_d.focusarc.exception.task.TaskDoesNotExistException;
import com.valentin_d.focusarc.model.id.ChapterId;
import com.valentin_d.focusarc.model.id.TaskId;
import com.valentin_d.focusarc.model.task.Task;
import com.valentin_d.focusarc.model.task.TaskStatus;
import com.valentin_d.focusarc.repository.TaskRepository;
import com.valentin_d.focusarc.service.BaseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TaskLoader extends BaseService {
    private final TaskRepository taskRepository;

    public Task getTaskIfExists(final TaskId taskId) {
        return fetchOrThrow(taskRepository, taskId, () -> new TaskDoesNotExistException(taskId));
    }

    public List<Task> getTasksForChapter(final ChapterId chapterId) {
        return taskRepository.findAllByChapter(chapterId);
    }

    public List<Task> getNotCompletedTaskForChapter(final ChapterId chapterId) {
        return taskRepository.findAllByChapterAndStatusIn(chapterId, TaskStatus.PENDING);
    }
}