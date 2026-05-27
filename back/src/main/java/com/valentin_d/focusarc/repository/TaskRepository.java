package com.valentin_d.focusarc.repository;

import com.valentin_d.focusarc.model.id.ChapterId;
import com.valentin_d.focusarc.model.id.TaskId;
import com.valentin_d.focusarc.model.task.Task;
import com.valentin_d.focusarc.model.task.TaskStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.Collection;
import java.util.List;

public interface TaskRepository extends MongoRepository<Task, TaskId> {
    List<Task> findAllByChapterOrderByStartAtAsc(final ChapterId chapterId);

    List<Task> findAllByChapterAndStatusIn(final ChapterId chapter, final Collection<TaskStatus> statuses);

    List<Task> findAllByChapterIn(Collection<ChapterId> chapterIds);

    boolean existsByChapterAndStatusInAndStartAtBeforeAndEndAtAfter(ChapterId chapter,
                                                                    Collection<TaskStatus> status,
                                                                    Instant startAt, Instant endAt);

    boolean existsByChapterAndStatusInAndIdNotAndStartAtBeforeAndEndAtAfter(ChapterId chapter,
            Collection<TaskStatus> status, TaskId id, Instant startAt, Instant endAt);

    boolean existsByChapterAndIdNotAndStatus(ChapterId chapter, TaskId id, TaskStatus status);

    void deleteAllByChapter(ChapterId chapterId);
}