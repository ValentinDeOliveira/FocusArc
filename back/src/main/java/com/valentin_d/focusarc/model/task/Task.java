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

    public Task(final TaskId taskId, final ChapterId chapterId, final int estimatedMinutes, final Instant scheduledAt) {
        this(taskId, chapterId, estimatedMinutes, 0, scheduledAt, TaskStatus.PLANNED);
    }

    public Task(final ChapterId chapterId, final int estimatedMinutes, final Instant scheduledAt) {
        this(TaskId.random(), chapterId, estimatedMinutes, scheduledAt);
    }
}