package com.valentin_d.focusarc.shared;

public final class TimeConstraints {
    private TimeConstraints() {}

    public static final int MINUTES_PER_DAY = 24 * 60;

    public static final int MAX_MINUTES_PER_CHAPTER = 20 * 60;
    // Max minute for a single task is 4 hours
    public static final int MAX_MINUTES_PER_TASK = 240;
}