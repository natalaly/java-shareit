package ru.practicum.shareit.exception;

public class UserAuthorizationException extends RuntimeException {

  public UserAuthorizationException(final String message) {
    super(message);
  }
}
