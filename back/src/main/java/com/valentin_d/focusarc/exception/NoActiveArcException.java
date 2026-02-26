package com.valentin_d.focusarc.exception;

import com.valentin_d.focusarc.model.id.UserId;

public class NoActiveArcException extends ApiException {
    public NoActiveArcException(final UserId id) {
        super("No active arc found for user: " + id.id());
    }
}