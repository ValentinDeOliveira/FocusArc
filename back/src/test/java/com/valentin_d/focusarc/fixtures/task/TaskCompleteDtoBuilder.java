package com.valentin_d.focusarc.fixtures.task;

import com.valentin_d.focusarc.dto.task.TaskCompleteDto;
import lombok.Builder;

@Builder
public class TaskCompleteDtoBuilder {
    @Builder.Default
    private final int completedMinutes = 140;


    public TaskCompleteDto build() {
        return new TaskCompleteDto(completedMinutes);
    }
}