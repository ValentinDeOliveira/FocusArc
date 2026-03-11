package com.valentin_d.focusarc.exception.task;

import com.valentin_d.focusarc.exception.ApiException;
import com.valentin_d.focusarc.model.id.ChapterId;

import java.time.Instant;

public class TaskOverlapException extends ApiException {
    public TaskOverlapException(final ChapterId chapterId, final Instant start, final Instant end) {
        super("Task overlaps for chapter " + chapterId + " at " + start + " to " + end);
    }
}