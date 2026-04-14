package com.valentin_d.focusarc.dto.tag;

import com.valentin_d.focusarc.model.id.TagId;

public record TagTaskStatsDto(TagId tagId,
                              Long total,
                              Long done) {
}