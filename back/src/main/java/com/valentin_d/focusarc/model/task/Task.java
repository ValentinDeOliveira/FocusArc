package com.valentin_d.focusarc.model.task;

import com.valentin_d.focusarc.model.id.ChapterId;
import com.valentin_d.focusarc.model.id.TagId;
import com.valentin_d.focusarc.model.id.TaskId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("tasks")
public class Task {
    @Id
    private TaskId id;
    private ChapterId chapter;
    private int estimatedMinutes;
    private int completedMinutes;
    private Instant startAt;
    private Instant startedAt;
    private Instant endAt;
    private Instant completedAt;
    private TaskStatus status;
    private String name;
    private String description;
    private TagId tagId;

    public Task(final Task task){
        this.id = task.getId();
        this.chapter = task.getChapter();
        this.estimatedMinutes = task.getEstimatedMinutes();
        this.completedMinutes = task.getCompletedMinutes();
        this.startAt = task.getStartAt();
        this.startedAt = task.getStartedAt();
        this.endAt = task.getEndAt();
        this.completedAt = task.getCompletedAt();
        this.status = task.getStatus();
        this.name = task.getName();
        this.description = task.getDescription();
        this.tagId = task.getTagId();
    }

    public Task(final TaskId taskId, final ChapterId chapterId, final int estimatedMinutes,
                final Instant scheduledAt, final String name, final String description,
                final TagId tag) {
        this(taskId, chapterId, estimatedMinutes, 0, scheduledAt, scheduledAt,
                scheduledAt.plus(estimatedMinutes, ChronoUnit.MINUTES),
                scheduledAt.plus(estimatedMinutes, ChronoUnit.MINUTES),
                TaskStatus.PLANNED, name, description, tag);
    }

    public Task(final TaskId taskId, final ChapterId chapterId, final int estimatedMinutes,
                final Instant scheduledAt, final String name, final String description) {
        this(taskId, chapterId, estimatedMinutes, 0, scheduledAt, scheduledAt,
                scheduledAt.plus(estimatedMinutes, ChronoUnit.MINUTES),
                scheduledAt.plus(estimatedMinutes, ChronoUnit.MINUTES),
                TaskStatus.PLANNED, name, description, null);
    }

    public Task(final ChapterId chapterId, final int estimatedMinutes, final Instant scheduledAt,
                final String name, final String description, final TagId tag) {
        this(TaskId.random(), chapterId, estimatedMinutes, scheduledAt, name, description, tag);
    }

    public Task(final ChapterId chapterId, final int estimatedMinutes, final Instant scheduledAt,
                final String name, final TagId tag) {
        this(TaskId.random(), chapterId, estimatedMinutes, scheduledAt, name, null, tag);
    }

    public Task(final ChapterId chapterId, final int estimatedMinutes, final Instant scheduledAt,
                final String name) {
        this(TaskId.random(), chapterId, estimatedMinutes, scheduledAt, name, null);
    }

    public boolean isDone() {
        return status == TaskStatus.DONE;
    }

    public boolean isEstimatedMinutesChanged(final Task other) {
        return estimatedMinutes != other.estimatedMinutes;
    }

    public boolean isCompletedMinutesChanged(final Task other) {
        return completedMinutes != other.completedMinutes;
    }

    public Task snapshot() {
        return new Task(this);
    }

    public void updateSchedule(final Instant startAt, final int estimatedMinutes) {
        this.startAt = startAt;
        this.estimatedMinutes = estimatedMinutes;
        this.endAt = startAt.plus(estimatedMinutes, ChronoUnit.MINUTES);
    }
}