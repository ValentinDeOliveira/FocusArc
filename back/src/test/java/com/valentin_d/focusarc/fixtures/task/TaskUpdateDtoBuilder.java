package com.valentin_d.focusarc.fixtures.task;

import com.valentin_d.focusarc.dto.task.TaskUpdateDto;
import lombok.Builder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Builder
public class TaskUpdateDtoBuilder {
    @Builder.Default
    private final short completedMinutes = 100;
    @Builder.Default
    private final short estimatedMinutes = 100;
    @Builder.Default
    private final Instant scheduledAt = Instant.now().truncatedTo(ChronoUnit.MILLIS);


    public TaskUpdateDto build() {
        return new TaskUpdateDto(completedMinutes, estimatedMinutes, scheduledAt);
    }
}