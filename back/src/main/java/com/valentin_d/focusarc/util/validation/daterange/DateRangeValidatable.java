package com.valentin_d.focusarc.util.validation.daterange;

import java.time.LocalDate;

public interface DateRangeValidatable {
    LocalDate startDate();
    LocalDate endDate();
}