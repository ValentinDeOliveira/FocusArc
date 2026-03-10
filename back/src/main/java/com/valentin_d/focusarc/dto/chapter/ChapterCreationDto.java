package com.valentin_d.focusarc.dto.chapter;

import com.valentin_d.focusarc.model.id.ArcId;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public record ChapterCreationDto(
        @NotNull ArcId arcId,
        @Positive int estimatedMinutes,
        @FutureOrPresent LocalDate scheduledDate
) {}