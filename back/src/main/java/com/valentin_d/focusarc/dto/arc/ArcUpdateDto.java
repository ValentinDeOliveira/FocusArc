package com.valentin_d.focusarc.dto.arc;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record ArcUpdateDto(@NotBlank String name, @Positive Integer totalEstimatedMinutes) {
}