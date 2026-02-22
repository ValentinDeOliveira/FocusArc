package com.valentin_d.focusarc.dto.task;

import com.valentin_d.focusarc.model.task.TaskStatus;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Positive;

import java.time.Instant;

// TODO: Check estimated minutes < completed minutes + estimated minutes | completed minutes < Short.MAX_VALUE
public record TaskUpdateDto(@Positive Integer completedMinutes, @Positive Integer estimatedMinutes,
                            @FutureOrPresent Instant scheduledAt, TaskStatus taskStatus) {
}