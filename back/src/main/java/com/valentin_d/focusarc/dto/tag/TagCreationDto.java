package com.valentin_d.focusarc.dto.tag;

import com.valentin_d.focusarc.model.tag.TagColor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TagCreationDto(@NotBlank String label, @NotNull TagColor color) {}
