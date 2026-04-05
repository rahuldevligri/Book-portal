package com.example.bookportal.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Annotation for validating that password and confirmation fields match.
 * Used on DTO classes to ensure password consistency.
 */
@Documented
@Constraint(validatedBy = PasswordMatchesValidator.class)
@Target({ TYPE })
@Retention(RUNTIME)
public @interface PasswordMatches {
    /**
     * Default error message when passwords do not match.
     * 
     * @return error message
     */
    String message() default "{password.mismatch}";

    /**
     * Allows specification of validation groups.
     * 
     * @return validation groups
     */
    Class<?>[] groups() default {};

    /**
     * Allows specification of custom payload objects.
     * 
     * @return custom payload
     */
    Class<? extends Payload>[] payload() default {};
}
