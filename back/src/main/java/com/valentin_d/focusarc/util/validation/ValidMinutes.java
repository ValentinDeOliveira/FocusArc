package com.valentin_d.focusarc.util.validation;

import com.valentin_d.focusarc.shared.TimeConstraints;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Positive
@Max(TimeConstraints.MAX_MINUTES_PER_TASK)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
public @interface ValidMinutes {
    String message() default "must be positive and at most " + TimeConstraints.MAX_MINUTES_PER_TASK;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}