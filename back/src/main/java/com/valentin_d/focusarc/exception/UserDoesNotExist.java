package com.valentin_d.focusarc.exception;

import com.valentin_d.focusarc.model.id.UserId;

public class UserDoesNotExist extends RuntimeException {
    public UserDoesNotExist(final UserId id) {
        super("User does not exist: " + id.id());
    }
}