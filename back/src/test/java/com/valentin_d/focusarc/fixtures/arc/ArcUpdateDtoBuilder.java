package com.valentin_d.focusarc.fixtures.arc;

import com.valentin_d.focusarc.dto.arc.ArcUpdateDto;
import lombok.Builder;

@Builder
public class ArcUpdateDtoBuilder {
    @Builder.Default
    private final String name = "Default Arc";

    @Builder.Default
    private final int totalPlannedMinutes = 120;

    public ArcUpdateDto build() {
        return new ArcUpdateDto(name, totalPlannedMinutes);
    }
}