package com.valentin_d.focusarc.exception.task;

import com.valentin_d.focusarc.exception.base.ApiException;
import com.valentin_d.focusarc.model.id.ChapterId;
import com.valentin_d.focusarc.model.id.TaskId;

public class TaskInProgressException extends ApiException {
    public TaskInProgressException(final ChapterId chapterId, final TaskId taskId) {
        super("Cannot start Task" + taskId.id() + " another task is in progress in " + chapterId.id());
    }
}