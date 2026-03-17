package com.valentin_d.focusarc.exception.task;

import com.valentin_d.focusarc.exception.base.ApiException;
import com.valentin_d.focusarc.model.id.TaskId;
import com.valentin_d.focusarc.model.task.TaskStatus;

public class TaskAlreadyFinishedException extends ApiException {
    public TaskAlreadyFinishedException(final TaskId id, final TaskStatus status) {
        super("Task " + id.id() + " already finished with status " + status.name());
    }
}