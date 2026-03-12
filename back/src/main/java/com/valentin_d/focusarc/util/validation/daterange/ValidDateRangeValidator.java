package com.valentin_d.focusarc.util.validation.daterange;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidDateRangeValidator implements ConstraintValidator<ValidDateRange, DateRangeValidatable> {
    @Override
    public boolean isValid(DateRangeValidatable value, ConstraintValidatorContext context) {
        if (value.startDate() == null || value.endDate() == null) return true;
        return value.endDate().isAfter(value.startDate());
    }
}