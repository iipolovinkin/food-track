package com.foodtracker.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UnsafeStringValidator.class)
public @interface UnsafeString {
    String message() default "Field contains unsafe content";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
