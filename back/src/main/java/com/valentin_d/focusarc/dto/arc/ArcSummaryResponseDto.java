package com.valentin_d.focusarc.dto.arc;

public record ArcSummaryResponseDto(
        int totalEstimatedMinutes,
        int totalCompletedMinutes,
        int remainingMinutes,
        int nbChapterCompleted,
        int nbChapterPlanned,
        int nbChapterSkipped,
        int daysStreak) {
}