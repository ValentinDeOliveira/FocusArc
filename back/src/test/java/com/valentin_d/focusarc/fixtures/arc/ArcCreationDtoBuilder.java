package com.valentin_d.focusarc.fixtures.arc;

import com.valentin_d.focusarc.dto.arc.ArcCreationDto;
import com.valentin_d.focusarc.model.id.UserId;
import lombok.Builder;

@Builder
public class ArcCreationDtoBuilder {
    @Builder.Default
    private final UserId ownerId = UserId.random();
    @Builder.Default
    private final String name = "Default Arc";
    @Builder.Default
    private final int totalPlannedMinutes = 120;

    public ArcCreationDto build() {
        return new ArcCreationDto(ownerId, name, totalPlannedMinutes);
    }
}