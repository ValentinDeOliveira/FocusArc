package com.valentin_d.focusarc.dto.task;

import com.valentin_d.focusarc.util.validation.ValidMinutes;

public record TaskCompleteDto(@ValidMinutes int completedMinutes){}