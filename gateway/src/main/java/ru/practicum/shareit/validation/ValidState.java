package ru.practicum.shareit.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The custom Annotation to validate that a parameter 'state' for the get method has valid value.
 */
@Documented
@Constraint(validatedBy = ValidStateValidator.class)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidState {

  String message() default "Unknown state: {state}";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};

}


