package com.valentin_d.focusarc.fixtures.arc;

import com.valentin_d.focusarc.dto.arc.ArcSummaryResponseDto;
import lombok.Builder;

@Builder
public class ArcSummaryResponseBuilder {
    @Builder.Default
    private final int totalEstimatedMinutes = 260;
    @Builder.Default
    private final int totalCompletedMinutes = 120;
    @Builder.Default
    private final int remainingMinutes = 140;
    @Builder.Default
    private final int nbChapterCompleted = 2;
    @Builder.Default
    private final int nbChapterPlanned = 1;
    @Builder.Default
    private final int nbChapterSkipped = 1;
    @Builder.Default
    private final int daysStreak = 2;

    public ArcSummaryResponseDto build() {
        return new ArcSummaryResponseDto(totalEstimatedMinutes, totalCompletedMinutes, remainingMinutes,
                nbChapterCompleted, nbChapterPlanned, nbChapterSkipped, daysStreak);
    }
}