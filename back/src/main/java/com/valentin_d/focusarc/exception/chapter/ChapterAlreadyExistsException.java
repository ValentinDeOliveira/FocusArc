package com.valentin_d.focusarc.exception.chapter;

import com.valentin_d.focusarc.exception.base.ApiException;
import com.valentin_d.focusarc.model.id.ArcId;

import java.time.LocalDate;

public class ChapterAlreadyExistsException extends ApiException {
    public ChapterAlreadyExistsException(final ArcId id, final LocalDate date) {
        super("Chapter already exists for arc " + id.id() + " for given date: " + date);
    }
}