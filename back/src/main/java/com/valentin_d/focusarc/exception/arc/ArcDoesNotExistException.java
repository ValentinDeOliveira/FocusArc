package com.valentin_d.focusarc.exception.arc;

import com.valentin_d.focusarc.exception.base.ApiException;
import com.valentin_d.focusarc.model.id.ArcId;
import org.springframework.http.HttpStatus;

public class ArcDoesNotExistException extends ApiException {
    public ArcDoesNotExistException(final ArcId id) {
        super("Arc does not exist: " + id.id(), HttpStatus.NOT_FOUND);
    }
}