package com.valentin_d.focusarc.dto.arc;

import com.valentin_d.focusarc.model.id.UserId;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record ArcCreationDto(UserId ownerId, @NotBlank String name, @Positive int totalEstimatedMinutes) {
}