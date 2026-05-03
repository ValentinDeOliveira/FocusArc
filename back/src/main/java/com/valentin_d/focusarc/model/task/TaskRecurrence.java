package com.valentin_d.focusarc.model.task;

import java.time.DayOfWeek;
import java.util.Set;

public sealed interface TaskRecurrence
        permits TaskRecurrence.Daily, TaskRecurrence.DaysOfWeek, TaskRecurrence.EveryNDays {

    record Daily() implements TaskRecurrence {}
    record DaysOfWeek(Set<DayOfWeek> days) implements TaskRecurrence {}
    record EveryNDays(int n) implements TaskRecurrence {}
}