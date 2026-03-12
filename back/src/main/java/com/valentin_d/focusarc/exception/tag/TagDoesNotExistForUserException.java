package com.valentin_d.focusarc.exception.tag;

import com.valentin_d.focusarc.exception.base.ApiException;
import org.springframework.http.HttpStatus;

public class TagDoesNotExistForUserException extends ApiException {
    public TagDoesNotExistForUserException() {
        super("Tag does not exist", HttpStatus.NOT_FOUND);
    }
}