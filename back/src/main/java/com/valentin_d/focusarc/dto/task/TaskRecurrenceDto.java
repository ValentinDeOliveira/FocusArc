package com.valentin_d.focusarc.dto.task;

import com.valentin_d.focusarc.model.id.TagId;
import com.valentin_d.focusarc.model.task.TaskRecurrence;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.time.Instant;

import static com.valentin_d.focusarc.shared.TimeConstraints.MINUTES_PER_DAY;

public record TaskRecurrenceDto(@Positive @Max(MINUTES_PER_DAY) int estimatedMinutes,
                                TaskRecurrence recurrence, Instant scheduledAt,
                                @NotBlank String name, @Nullable TagId tagId) {
}