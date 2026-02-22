package com.valentin_d.focusarc.dto.task;

import com.valentin_d.focusarc.model.id.ChapterId;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.Instant;

public record TaskCreationDto(@NotNull ChapterId chapterId, @Positive short estimatedMinutes, @FutureOrPresent Instant scheduledAt) {
}