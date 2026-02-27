package com.valentin_d.focusarc.dto.chapter;

import jakarta.validation.constraints.Future;

import java.time.LocalDate;

public record ChapterUpdateDto(@Future LocalDate scheduledDate) {
}