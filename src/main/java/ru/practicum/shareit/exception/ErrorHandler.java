package ru.practicum.shareit.exception;


import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice("ru.practicum.shareit")
@Slf4j
public class ErrorHandler {

  @ExceptionHandler
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ValidationErrorResponse handlerMethodArgumentNotValidException(
      final MethodArgumentNotValidException e) {

    final List<String> errorMessages = e.getBindingResult().getAllErrors()
        .stream()
        .map(DefaultMessageSourceResolvable::getDefaultMessage)
        .toList();

    log.warn("MethodArgumentNotValidException was thrown: {}", e.getMessage());
    return new ValidationErrorResponse(errorMessages);
  }

  @ExceptionHandler
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ErrorResponse handleValidationException(final ValidationException e) {
    log.error("Validation exception was thrown: {}", e.getMessage());
    return new ErrorResponse(e.getMessage());
  }

  @ExceptionHandler
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public ErrorResponse handleUserAuthorizationException(final UserAuthorizationException e) {
    log.error("UserAuthorizationException exception was thrown: {}", e.getMessage());
    return new ErrorResponse(e.getMessage());
  }

  @ExceptionHandler
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorResponse handlerNotFoundException(final NotFoundException e) {
    log.warn("NotFoundException was thrown: {}", e.getMessage());
    return new ErrorResponse(e.getMessage());
  }

  @ExceptionHandler
  @ResponseStatus(HttpStatus.CONFLICT)
  public ErrorResponse handlerDuplicatedDataException(final DuplicatedDataException e) {
    log.warn("DuplicatedDataException was thrown: {}", e.getMessage());
    return new ErrorResponse(e.getMessage());
  }

  @ExceptionHandler
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorResponse handleDatabaseException(final DatabaseException e) {
    log.error("DatabaseException was thrown: {}", e.getMessage());
    return new ErrorResponse("Internal server error.");
  }

  @ExceptionHandler
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorResponse handleAllExceptions(final Exception e) {
    log.error("Unexpected error occurred: ", e);
    return new ErrorResponse("Internal server error.");
  }

  @ExceptionHandler
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorResponse handleConstraintViolationException(final ConstraintViolationException e) {
    log.error("Unexpected error occurred: {}", e.getMessage());
    final String message = e.getConstraintViolations()
        .stream()
        .map(ConstraintViolation::getMessage)
        .findFirst()
        .orElse("Invalid request parameter.");
    return new ErrorResponse(message);
  }


}
