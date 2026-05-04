package com.valentin_d.focusarc.fixtures.task;

import com.valentin_d.focusarc.dto.task.TaskRecurrenceDto;
import com.valentin_d.focusarc.model.id.TagId;
import com.valentin_d.focusarc.model.task.TaskRecurrence;
import lombok.Builder;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

@Builder
public class TaskRecurrenceDtoBuilder {
    @Builder.Default
    private final int estimatedMinutes = 60;
    @Builder.Default
    private final TaskRecurrence recurrence = new TaskRecurrence.Daily();
    @Builder.Default
    private final Instant scheduledAt = Instant.now().truncatedTo(ChronoUnit.MILLIS).plusSeconds(120);
    @Builder.Default
    private final String name = "My task";
    @Builder.Default
    private final TagId tag = null;


    public TaskRecurrenceDto build() {
        return new TaskRecurrenceDto(estimatedMinutes, recurrence, scheduledAt,
                name, tag);
    }
}