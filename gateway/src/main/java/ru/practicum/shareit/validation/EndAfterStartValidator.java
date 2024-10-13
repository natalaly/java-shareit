package ru.practicum.shareit.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import ru.practicum.shareit.booking.dto.BookingDto;

/**
 * Validator class for the {@link EndAfterStart} annotation.
 * <p>
 * This class checks if the {@code end} date of a {@link BookingDto} is after the {@code start}
 * date. It is used to ensure that a booking has a valid time period.
 * <p>
 * The validation does not handle null-checking.
 *
 * @see EndAfterStart
 * @see BookingDto
 */
public class EndAfterStartValidator implements ConstraintValidator<EndAfterStart, BookingDto> {

  @Override
  public boolean isValid(BookingDto bookingDto, ConstraintValidatorContext context) {
    if (bookingDto.getStart() == null || bookingDto.getEnd() == null) {
      return true;
    }
    return bookingDto.getEnd().isAfter(bookingDto.getStart());
  }
}
