package com.valentin_d.focusarc.util.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static com.valentin_d.focusarc.shared.SizeConstraints.EMAIL_MAX_SIZE;

@NotBlank
@Size(max = EMAIL_MAX_SIZE)
@Email
@Target({ElementType.FIELD, ElementType.METHOD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {})
public @interface ValidEmail {
    String message() default "invalid email format";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}