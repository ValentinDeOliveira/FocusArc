package com.valentin_d.focusarc.exception.task;

import com.valentin_d.focusarc.exception.ApiException;
import com.valentin_d.focusarc.model.id.TaskId;

public class TaskInvalidMinuteException extends ApiException {
    public TaskInvalidMinuteException(final TaskId id, final int minutes) {
        super("wrong minutes: " + minutes + " for task: " + id.id());
    }
}