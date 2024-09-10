package ru.practicum.shareit.exception;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice("ru.practicum.shareit")
public class ErrorHandler {

  @ExceptionHandler
  @ResponseStatus(HttpStatus.NOT_FOUND)
  public ErrorResponse handlerNotFoundException(final NotFoundException e) {
    return new ErrorResponse(e.getMessage());
  }

  @ExceptionHandler
  @ResponseStatus(HttpStatus.CONFLICT)
  public ErrorResponse handlerDuplicatedDataException(final DuplicatedDataException e) {
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

    return new ValidationErrorResponse(errors);
  }

}
