package com.valentin_d.focusarc.dto.task;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import static com.valentin_d.focusarc.shared.TimeConstraints.MINUTES_PER_DAY;

public record TaskCompleteDto(@NotNull @Positive @Max(MINUTES_PER_DAY) int completedMinutes){}