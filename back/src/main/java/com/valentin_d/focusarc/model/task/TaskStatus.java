package com.valentin_d.focusarc.model.task;

import java.util.Set;

public enum TaskStatus {
    PLANNED,
    IN_PROGRESS,
    DONE,
    SKIPPED;

    public static final Set<TaskStatus> FINISHED = Set.of(DONE, SKIPPED);
    public static final Set<TaskStatus> PENDING = Set.of(PLANNED, IN_PROGRESS);

    public boolean isFinished() {
        return FINISHED.contains(this);
    }

    public boolean isPending() {
        return PENDING.contains(this);
    }
}