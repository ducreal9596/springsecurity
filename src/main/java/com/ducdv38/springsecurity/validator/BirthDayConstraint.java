package com.ducdv38.springsecurity.validator;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = {BirthDayValidator.class})
public @interface BirthDayConstraint {

    String message() default "Invalid birthday";

    int min();

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
