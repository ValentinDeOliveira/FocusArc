package com.valentin_d.focusarc.fixtures.arc;

import com.valentin_d.focusarc.dto.arc.ArcUpdateDto;
import lombok.Builder;

import java.time.LocalDate;

@Builder
public class ArcUpdateDtoBuilder {
    @Builder.Default
    private final String name = "Default Arc";
    @Builder.Default
    private final Integer totalEstimatedMinutes = 120;
    @Builder.Default
    private final LocalDate startDate = LocalDate.now();
    @Builder.Default
    private final LocalDate endDate = LocalDate.now().plusDays(10);

    public ArcUpdateDto build() {
        return new ArcUpdateDto(name, totalEstimatedMinutes,  startDate, endDate);
    }
}