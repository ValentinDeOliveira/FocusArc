package com.valentin_d.focusarc.exception.tag;

import com.valentin_d.focusarc.exception.ApiException;
import com.valentin_d.focusarc.model.id.TagId;
import org.springframework.http.HttpStatus;

public class TagDoesNotExistException extends ApiException {
    public TagDoesNotExistException(final TagId tagId) {
        super("Tag does not exist: " + tagId.id(), HttpStatus.NOT_FOUND);
    }
}
