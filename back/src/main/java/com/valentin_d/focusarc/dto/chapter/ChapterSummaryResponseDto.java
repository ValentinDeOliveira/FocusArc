package com.valentin_d.focusarc.dto.chapter;

import com.valentin_d.focusarc.model.id.ChapterId;
import com.valentin_d.focusarc.model.task.Task;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;

import java.util.List;

import static com.valentin_d.focusarc.shared.TimeConstraints.MAX_MINUTES_PER_CHAPTER;

public record ChapterSummaryResponseDto(ChapterId chapterId,
                                        List<Task> tasksToComplete,
                                        @Positive @Max(MAX_MINUTES_PER_CHAPTER) int estimatedMinutes,
                                        @Positive int completedMinutes,
                                        @Positive int remainingTime) {
}