package com.valentin_d.focusarc.dto.arc;

import com.valentin_d.focusarc.util.validation.daterange.DateRangeValidatable;
import com.valentin_d.focusarc.util.validation.daterange.ValidDateRange;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

import static com.valentin_d.focusarc.shared.SizeConstraints.ARC_NAME_MAX_LENGTH;

@ValidDateRange
public record ArcCreationDto(@NotBlank @Size(max = ARC_NAME_MAX_LENGTH) String name,
                             @FutureOrPresent LocalDate startDate,
                             @Future LocalDate endDate)
        implements DateRangeValidatable {
}