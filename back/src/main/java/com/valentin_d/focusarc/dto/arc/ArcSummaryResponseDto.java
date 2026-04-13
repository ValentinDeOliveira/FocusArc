package com.valentin_d.focusarc.dto.arc;

import com.valentin_d.focusarc.model.id.ArcId;

public record ArcSummaryResponseDto(
        ArcId arcId,
        String name,
        int totalEstimatedMinutes,
        int totalCompletedMinutes,
        int remainingMinutes,
        int nbChapterCompleted,
        int nbChapterPlanned,
        int nbChapterSkipped,
        int daysStreak) {
}