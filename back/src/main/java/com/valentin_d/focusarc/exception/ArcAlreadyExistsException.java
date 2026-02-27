package com.valentin_d.focusarc.exception;

import com.valentin_d.focusarc.model.id.UserId;

public class ArcAlreadyExistsException extends ApiException {
    public ArcAlreadyExistsException(final UserId id) {
        super("Arc already active for user: " + id.id());
    }
}