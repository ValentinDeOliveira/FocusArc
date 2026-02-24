package com.valentin_d.focusarc.model.task;

import com.valentin_d.focusarc.model.id.ChapterId;
import com.valentin_d.focusarc.model.id.TaskId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

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
    private Instant scheduledAt;
    private TaskStatus status;

    public Task(final Task task){
        this.id = task.getId();
        this.chapter = task.getChapter();
        this.estimatedMinutes = task.getEstimatedMinutes();
        this.completedMinutes = task.getCompletedMinutes();
        this.scheduledAt = task.getScheduledAt();
        this.status = task.getStatus();
    }

    public Task(final TaskId taskId, final ChapterId chapterId, final int estimatedMinutes, final Instant scheduledAt) {
        this(taskId, chapterId, estimatedMinutes, 0, scheduledAt, TaskStatus.PLANNED);
    }

    public Task(final ChapterId chapterId, final int estimatedMinutes, final Instant scheduledAt) {
        this(TaskId.random(), chapterId, estimatedMinutes, scheduledAt);
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
}