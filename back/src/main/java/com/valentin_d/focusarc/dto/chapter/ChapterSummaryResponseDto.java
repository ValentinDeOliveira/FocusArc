package com.valentin_d.focusarc.dto.chapter;

import com.valentin_d.focusarc.model.task.Task;
import jakarta.validation.constraints.Min;

import java.util.List;

public record ChapterSummaryResponseDto(List<Task> tasksToComplete,
                                        @Min(0) int estimatedMinutes,
                                        @Min(0) int completedMinutes,
                                        @Min(0) int remainingTime) {
}