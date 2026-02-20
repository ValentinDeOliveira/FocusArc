package com.valentin_d.focusarc.exception;

import com.valentin_d.focusarc.model.id.UserId;
import org.springframework.http.HttpStatus;

public class UserDoesNotExistException extends ApiException {
    public UserDoesNotExistException(final UserId id) {
        super("User does not exist: " + id.id(), HttpStatus.NOT_FOUND);
    }
}