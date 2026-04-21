package com.valentin_d.focusarc.dto.task;

import com.valentin_d.focusarc.model.task.TaskStatus;

public record TaskStatsDto(TaskStatus taskStatus,
                           Long total,
                           Long done) {
}