package com.valentin_d.focusarc.repository;

import com.valentin_d.focusarc.model.id.ChapterId;
import com.valentin_d.focusarc.model.id.TaskId;
import com.valentin_d.focusarc.model.task.Task;
import com.valentin_d.focusarc.model.task.TaskStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Collection;
import java.util.List;

public interface TaskRepository extends MongoRepository<Task, TaskId> {
    List<Task> findAllByChapter(final ChapterId chapterId);

    List<Task> findAllByChapterAndStatusIn(final ChapterId chapter, final Collection<TaskStatus> statuses);
}