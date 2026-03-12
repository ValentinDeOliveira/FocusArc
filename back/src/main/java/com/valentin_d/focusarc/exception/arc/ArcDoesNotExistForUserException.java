package com.valentin_d.focusarc.exception.arc;

import com.valentin_d.focusarc.exception.base.ApiException;
import com.valentin_d.focusarc.model.id.ArcId;
import com.valentin_d.focusarc.model.id.UserId;
import org.springframework.http.HttpStatus;

public class ArcDoesNotExistForUserException extends ApiException {
    public ArcDoesNotExistForUserException(final ArcId id, final UserId owner) {
        super("Arc does not exist: " + id.id() + " for user: " + owner.id(), HttpStatus.NOT_FOUND);
    }
}