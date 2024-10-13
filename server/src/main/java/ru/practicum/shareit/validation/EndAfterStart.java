package ru.practicum.shareit.validation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import ru.practicum.shareit.booking.dto.BookingDto;

/**
 * Custom annotation to validate that the {@code end} field in the {@link BookingDto} is after the
 * {@code start} field. It is applied at the class level and is validated by the
 * {@link EndAfterStartValidator}.
 * <p>
 * The validation does not handle null-checking.
 * <p>
 * It supports grouping of validation using the {@code groups} attribute, which defaults to the
 * {@code Create.class} group.
 *
 * @see EndAfterStartValidator
 * @see BookingDto
 */
@Documented
//@Constraint(validatedBy = EndAfterStartValidator.class)
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EndAfterStart {

  String message() default "Invalid date. End date should be after start date.";

  Class<?>[] groups() default {};

//  Class<? extends Payload>[] payload() default {};
}
