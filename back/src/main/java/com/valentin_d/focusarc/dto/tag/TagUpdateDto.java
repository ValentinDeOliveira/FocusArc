package com.valentin_d.focusarc.dto.tag;

import com.valentin_d.focusarc.model.tag.TagColor;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import static com.valentin_d.focusarc.shared.SizeConstraints.MID_MAX_LENGTH;

@Builder
public record TagUpdateDto(@Size(max = MID_MAX_LENGTH) String label, TagColor color) {}