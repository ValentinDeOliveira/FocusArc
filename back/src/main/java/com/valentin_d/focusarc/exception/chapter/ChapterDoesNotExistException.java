package com.valentin_d.focusarc.exception.chapter;

import com.valentin_d.focusarc.exception.base.ApiException;
import com.valentin_d.focusarc.model.id.ChapterId;
import org.springframework.http.HttpStatus;

public class ChapterDoesNotExistException extends ApiException {
    public ChapterDoesNotExistException(final ChapterId id) {
        super("Chapter does not exist: " + id.id(), HttpStatus.NOT_FOUND);
    }
}