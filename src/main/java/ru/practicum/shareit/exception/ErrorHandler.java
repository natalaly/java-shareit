package ru.practicum.shareit.exception;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice("ru.practicum.shareit")
@Slf4j
public class ErrorHandler {

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
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ValidationErrorResponse handlerMethodArgumentNotValidException(
      final MethodArgumentNotValidException e) {

    final Map<String, List<String>> errors = new HashMap<>();

    e.getBindingResult().getAllErrors()
        .forEach(violation -> {
              String fieldName = ((FieldError) violation).getField();
              String errorMessage = violation.getDefaultMessage();
              errors.computeIfAbsent(fieldName, k -> new ArrayList<>()).add(errorMessage);
            }
        );
    log.warn("MethodArgumentNotValidException was thrown: {}", e.getMessage());
    return new ValidationErrorResponse(errors);
  }

  @ExceptionHandler
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ErrorResponse handleAllExceptions(final Exception e) {
    log.error("Unexpected error occurred: ", e);
    return new ErrorResponse(e.getMessage());
  }

}
