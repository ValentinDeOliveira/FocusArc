package com.valentin_d.focusarc.dto.tag;

import com.valentin_d.focusarc.model.tag.TagColor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import static com.valentin_d.focusarc.shared.SizeConstraints.MID_MAX_LENGTH;

public record TagCreationDto(@NotBlank @Size(max = MID_MAX_LENGTH) String label,
                             @NotNull TagColor color) {}