package com.valentin_d.focusarc.dto.tag;

import com.valentin_d.focusarc.model.tag.TagColor;
import lombok.Builder;

@Builder
public record TagUpdateDto(String label, TagColor color) {}
