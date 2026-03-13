package com.valentin_d.focusarc.fixtures.arc;

import com.valentin_d.focusarc.dto.arc.ArcCreationDto;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public class ArcCreationDtoBuilder {
    @Builder.Default
    private final String name = "Default Arc";
    @Builder.Default
    private final int totalEstimatedMinutes = 120;
    @Builder.Default
    private final LocalDate startDate = LocalDate.now();
    @Builder.Default
    private final LocalDate endDate = LocalDate.now().plusDays(10);

    public ArcCreationDto build() {
        return new ArcCreationDto(name, totalEstimatedMinutes, startDate, endDate);
    }
}