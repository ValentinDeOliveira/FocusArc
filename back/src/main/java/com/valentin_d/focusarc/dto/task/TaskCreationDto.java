package com.valentin_d.focusarc.dto.task;

import com.valentin_d.focusarc.model.id.ChapterId;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.Instant;

import static com.valentin_d.focusarc.shared.TimeConstraints.MINUTES_PER_DAY;

public record TaskCreationDto(@NotNull ChapterId chapterId, @Positive @Max(MINUTES_PER_DAY) int estimatedMinutes,
                              @FutureOrPresent Instant scheduledAt) {
}