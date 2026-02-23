package com.valentin_d.focusarc.dto.arc;

import jakarta.validation.constraints.Positive;

public record ArcUpdateDto(String name, @Positive Integer totalEstimatedMinutes) {
}