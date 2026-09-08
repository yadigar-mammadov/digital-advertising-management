package com.digitaladvertisingmanagement.auth.application.exception;

public class InvalidCredentialsException extends RuntimeException {
  public InvalidCredentialsException(String message) {
    super(message);
  }

  public InvalidCredentialsException() {
    super("Email or password is incorrect.");
  }
}
