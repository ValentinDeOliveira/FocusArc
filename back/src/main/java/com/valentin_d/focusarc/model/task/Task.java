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
    private short estimatedMinutes;
    private short completedMinutes;
    private Instant scheduledAt;

    public Task(final TaskId taskId, final ChapterId chapterId, final short estimatedMinutes, final Instant scheduledAt) {
        this(taskId, chapterId, estimatedMinutes, (short) 0, scheduledAt);
    }

    public Task(final ChapterId chapterId, final short estimatedMinutes, final Instant scheduledAt) {
        this(TaskId.random(), chapterId, estimatedMinutes, scheduledAt);
    }

    public Task(final ChapterId chapterId, final short estimatedMinutes) {
        this(TaskId.random(), chapterId, estimatedMinutes, Instant.now());
    }
}