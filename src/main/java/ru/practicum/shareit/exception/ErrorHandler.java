package ru.practicum.shareit.exception;


import org.springframework.http.HttpStatus;
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

//  @ExceptionHandler
//  @ResponseStatus(HttpStatus.BAD_REQUEST)
//  public ErrorResponse handlerInvalidDataException(final InvalidDataException e) {
//    return new ErrorResponse(e.getMessage());
//  }
//
//  @ExceptionHandler
//  @ResponseStatus(HttpStatus.BAD_REQUEST)
//  public ErrorResponse handlerDuplicatedDataException(final DuplicatedDataException e) {
//    return new ErrorResponse(e.getMessage());
//  }
//
//  @ExceptionHandler
//  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//  public ErrorResponse handlerInternalServerException(final InternalServerException e) {
//    return new ErrorResponse(e.getMessage());
//  }
//

}
