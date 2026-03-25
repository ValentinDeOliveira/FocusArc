package com.valentin_d.focusarc.dto.task;

import com.valentin_d.focusarc.model.id.TagId;
import com.valentin_d.focusarc.model.task.TaskStatus;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Positive;
import lombok.Builder;

import java.time.Instant;

// TODO: Check estimated minutes < completed minutes + estimated minutes | completed minutes < Short.MAX_VALUE
@Builder
public record TaskUpdateDto(@Positive Integer completedMinutes, @Positive Integer estimatedMinutes,
                            @FutureOrPresent Instant scheduledAt, TaskStatus taskStatus,
                            String name, String description, @Nullable TagId tagId) {
}