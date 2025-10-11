package com.foodtracker.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PropertiesValidator.class)
public @interface ValidProperties {
    String message() default "Properties contain invalid content";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}