package com.valentin_d.focusarc.fixtures.task;

import com.valentin_d.focusarc.dto.task.TaskCreationDto;
import com.valentin_d.focusarc.model.id.ChapterId;
import lombok.Builder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Builder
public class TaskCreationDtoBuilder {
    @Builder.Default
    private final ChapterId chapterId = ChapterId.random();
    @Builder.Default
    private final int estimatedMinutes = 120;
    @Builder.Default
    private final Instant scheduledAt = Instant.now().truncatedTo(ChronoUnit.MILLIS).plusSeconds(120);

    public TaskCreationDto build() {
        return new TaskCreationDto(chapterId, estimatedMinutes, scheduledAt);
    }
}