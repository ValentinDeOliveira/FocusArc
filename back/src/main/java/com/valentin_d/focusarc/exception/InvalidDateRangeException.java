package com.valentin_d.focusarc.exception;

import com.valentin_d.focusarc.exception.base.ApiException;

import java.time.LocalDate;

public class InvalidDateRangeException extends ApiException {
    public InvalidDateRangeException(final LocalDate start, final LocalDate end) {
        super("invalid date set with " + start.toString() + " to " + end.toString());
    }
}