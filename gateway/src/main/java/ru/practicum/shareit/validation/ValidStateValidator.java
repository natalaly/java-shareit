package ru.practicum.shareit.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.booking.dto.BookingState;

/**
 * Validator for the {@link ValidState} annotation. Check if a 'state' parameter has correct value
 * that has been specified in constrains.
 */

@Slf4j
public class ValidStateValidator implements ConstraintValidator<ValidState, String> {

  private List<String> validStateOptions;

  @Override
  public void initialize(ValidState constraintAnnotation) {
    validStateOptions = BookingState.getValidStates();
  }

  @Override
  public boolean isValid(String state, ConstraintValidatorContext context) {
    log.debug("Validating state parameter value = {}.", state);
    if (state == null || state.isEmpty()) {
      return true;
    }
    if (!validStateOptions.contains(state.trim().toUpperCase())) {
      context.disableDefaultConstraintViolation();
      context.buildConstraintViolationWithTemplate("Unknown state: " + state)
          .addConstraintViolation();
      return false;
    }
    log.debug("Success in validating state parameter value = {}", state);
    return true;
  }

}