package com.valentin_d.focusarc.dto.chapter;

import jakarta.validation.constraints.Positive;

public record ChapterUpdateDto(@Positive Integer completedMinutes, @Positive Integer estimatedMinutes) {
}