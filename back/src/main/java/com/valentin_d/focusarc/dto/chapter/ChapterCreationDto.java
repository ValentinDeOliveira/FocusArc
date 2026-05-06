package com.valentin_d.focusarc.dto.chapter;

import com.valentin_d.focusarc.model.id.ArcId;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

import static com.valentin_d.focusarc.shared.TimeConstraints.MAX_MINUTES_PER_CHAPTER;

public record ChapterCreationDto(
        @NotNull ArcId arcId,
        @Positive @Max(MAX_MINUTES_PER_CHAPTER) int estimatedMinutes,
        @FutureOrPresent LocalDate scheduledDate
) {}