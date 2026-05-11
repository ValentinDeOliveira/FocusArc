package com.valentin_d.focusarc.dto.task;

import com.valentin_d.focusarc.model.id.ChapterId;
import com.valentin_d.focusarc.model.id.TagId;
import com.valentin_d.focusarc.util.validation.ValidMinutes;
import jakarta.annotation.Nullable;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.Instant;

import static com.valentin_d.focusarc.shared.SizeConstraints.MID_MAX_LENGTH;

public record TaskCreationDto(@NotNull ChapterId chapterId, @ValidMinutes int estimatedMinutes,
                              @FutureOrPresent Instant scheduledAt,
                              @NotBlank @Size(max = MID_MAX_LENGTH) String name,
                              @Nullable String description, @Nullable TagId tagId) {
}