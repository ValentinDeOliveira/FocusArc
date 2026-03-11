package com.valentin_d.focusarc.fixtures.task;

import com.valentin_d.focusarc.dto.task.TaskUpdateDto;
import com.valentin_d.focusarc.model.task.TaskStatus;
import lombok.Builder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Builder
public class TaskUpdateDtoBuilder {
    @Builder.Default
    private final int completedMinutes = 100;
    @Builder.Default
    private final int estimatedMinutes = 100;
    @Builder.Default
    private final Instant scheduledAt = Instant.now().truncatedTo(ChronoUnit.MILLIS).plusSeconds(120);
    @Builder.Default
    private final TaskStatus taskStatus = TaskStatus.PLANNED;
    @Builder.Default
    private final String name = "My task";
    @Builder.Default
    private final String description = "My description";


    public TaskUpdateDto build() {
        return new TaskUpdateDto(completedMinutes, estimatedMinutes, scheduledAt, taskStatus, name, description);
    }
}