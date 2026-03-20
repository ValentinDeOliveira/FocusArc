package com.valentin_d.focusarc.dto.task;

import com.valentin_d.focusarc.model.id.ChapterId;
import com.valentin_d.focusarc.model.id.TagId;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.*;

import java.time.Instant;

import static com.valentin_d.focusarc.shared.TimeConstraints.MINUTES_PER_DAY;

public record TaskCreationDto(@NotNull ChapterId chapterId, @Positive @Max(MINUTES_PER_DAY) int estimatedMinutes,
                              @FutureOrPresent Instant scheduledAt, @NotBlank String name,
                              @Nullable String description, @Nullable TagId tagId) {
}