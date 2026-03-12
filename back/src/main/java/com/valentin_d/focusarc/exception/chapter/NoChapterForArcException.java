package com.valentin_d.focusarc.exception.chapter;

import com.valentin_d.focusarc.exception.base.ApiException;
import com.valentin_d.focusarc.model.id.ArcId;
import org.springframework.http.HttpStatus;

import java.time.LocalDate;

public class NoChapterForArcException extends ApiException {
    public NoChapterForArcException(final ArcId id, final LocalDate date) {
        super("No chapter found for arc: " + id.id() + " at date: " + date, HttpStatus.NOT_FOUND);
    }
}