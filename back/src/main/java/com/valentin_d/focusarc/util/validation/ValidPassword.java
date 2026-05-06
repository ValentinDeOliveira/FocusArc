package com.valentin_d.focusarc.util.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.valentin_d.focusarc.shared.SizeConstraints.PASSWORD_MIN_SIZE;

@NotBlank
@Size(min = PASSWORD_MIN_SIZE)
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
public @interface ValidPassword {
    String message() default "invalid password format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}