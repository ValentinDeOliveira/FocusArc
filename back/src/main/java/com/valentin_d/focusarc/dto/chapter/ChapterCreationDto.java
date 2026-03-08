package com.valentin_d.focusarc.dto.chapter;

import com.valentin_d.focusarc.model.id.ArcId;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

@Schema(description = "Chapter creation request")
public record ChapterCreationDto(
        @Schema(description = "ID of the arc this chapter belongs to", example = "00000000-0000-0000-0000-000000000002")
        @NotNull ArcId arcId,

        @Schema(description = "Planned minutes for this day — must be positive", example = "120")
        @Positive int estimatedMinutes,

        @Schema(description = "Scheduled date — today or in the future (YYYY-MM-DD)", example = "2026-03-15")
        @FutureOrPresent LocalDate scheduledDate
) {}
