package com.valentin_d.focusarc.exception.task;

import com.valentin_d.focusarc.exception.base.ApiException;
import com.valentin_d.focusarc.model.id.TaskId;
import org.springframework.http.HttpStatus;

public class TaskDoesNotExistException extends ApiException {
    public TaskDoesNotExistException(final TaskId id) {
        super("Task does not exist: " + id.id(), HttpStatus.NOT_FOUND);
    }
}