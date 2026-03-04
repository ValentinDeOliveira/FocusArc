package com.valentin_d.focusarc.fixtures.arc;

import com.valentin_d.focusarc.dto.arc.ArcCreationDto;
import lombok.Builder;

@Builder
public class ArcCreationDtoBuilder {
    @Builder.Default
    private final String name = "Default Arc";
    @Builder.Default
    private final int totalEstimatedMinutes = 120;

    public ArcCreationDto build() {
        return new ArcCreationDto(name, totalEstimatedMinutes);
    }
}