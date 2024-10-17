package ru.practicum.shareit.exception;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice("ru.practicum.shareit")
@Slf4j
public class ErrorHandler {

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

}
