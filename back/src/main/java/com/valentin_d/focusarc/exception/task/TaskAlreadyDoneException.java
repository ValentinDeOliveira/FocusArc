package com.valentin_d.focusarc.exception.task;

import com.valentin_d.focusarc.exception.ApiException;
import com.valentin_d.focusarc.model.id.TaskId;

public class TaskAlreadyDoneException extends ApiException {
    public TaskAlreadyDoneException(final TaskId id) {
        super("Task already done: " + id.id());
    }
}