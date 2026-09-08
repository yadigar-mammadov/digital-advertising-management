package com.digitaladvertisingmanagement.campaignmanagement.domain.exception;

public class DomainValidationException extends RuntimeException {
  public DomainValidationException(String message) {
    super(message);
  }
}
