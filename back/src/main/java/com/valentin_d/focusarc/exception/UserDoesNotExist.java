package com.valentin_d.focusarc.exception;

import com.valentin_d.focusarc.model.id.UserId;
import org.springframework.http.HttpStatus;

public class UserDoesNotExist extends ApiException {
    public UserDoesNotExist(final UserId id) {
        super("User does not exist: " + id.id(), HttpStatus.NOT_FOUND);
    }
}