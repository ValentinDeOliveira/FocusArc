package com.valentin_d.focusarc.dto.arc;

import com.valentin_d.focusarc.util.validation.daterange.DateRangeValidatable;
import com.valentin_d.focusarc.util.validation.daterange.ValidDateRange;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

@ValidDateRange
public record ArcCreationDto(@NotBlank String name,
                             @FutureOrPresent LocalDate startDate,
                             @Future LocalDate endDate)
        implements DateRangeValidatable {
}