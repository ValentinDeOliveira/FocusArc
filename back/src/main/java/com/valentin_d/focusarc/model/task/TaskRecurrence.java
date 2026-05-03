package com.valentin_d.focusarc.model.task;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.time.DayOfWeek;
import java.util.Set;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = TaskRecurrence.Daily.class, name = "DAILY"),
        @JsonSubTypes.Type(value = TaskRecurrence.EveryNDays.class, name = "EVERY_N_DAYS"),
        @JsonSubTypes.Type(value = TaskRecurrence.DaysOfWeek.class, name = "DAYS_OF_WEEK")
})
public sealed interface TaskRecurrence
        permits TaskRecurrence.Daily, TaskRecurrence.DaysOfWeek, TaskRecurrence.EveryNDays {

    record Daily() implements TaskRecurrence {}
    record DaysOfWeek(Set<DayOfWeek> days) implements TaskRecurrence {}
    record EveryNDays(int n) implements TaskRecurrence {}
}