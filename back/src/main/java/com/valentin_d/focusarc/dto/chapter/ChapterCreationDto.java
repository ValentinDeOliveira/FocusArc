package com.valentin_d.focusarc.dto.chapter;

import com.valentin_d.focusarc.model.id.ArcId;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ChapterCreationDto(@NotNull ArcId arcId, @Positive int estimatedMinutes) {
}