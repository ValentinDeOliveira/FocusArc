package com.valentin_d.focusarc.fixtures.task;

import com.valentin_d.focusarc.model.id.ChapterId;
import com.valentin_d.focusarc.model.id.TaskId;
import com.valentin_d.focusarc.model.task.Task;
import com.valentin_d.focusarc.model.task.TaskStatus;
import lombok.Builder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Builder
public class TaskBuilder {
    @Builder.Default
    private final TaskId id = TaskId.random();
    @Builder.Default
    private final ChapterId chapter = ChapterId.random();
    @Builder.Default
    private final int estimatedMinutes = 220;
    @Builder.Default
    private final int completedMinutes = 130;
    @Builder.Default
    private final Instant scheduledAt = Instant.now().truncatedTo(ChronoUnit.MILLIS).plusSeconds(120);
    @Builder.Default
    private final TaskStatus taskStatus = TaskStatus.PLANNED;

    public Task build() {
        return new Task(id, chapter, estimatedMinutes, completedMinutes, scheduledAt, taskStatus);
    }
}