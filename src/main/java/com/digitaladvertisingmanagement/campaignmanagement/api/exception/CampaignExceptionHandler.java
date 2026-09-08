package com.digitaladvertisingmanagement.campaignmanagement.api.exception;

import com.digitaladvertisingmanagement.campaignmanagement.application.exception.CampaignGroupNotFoundException;
import com.digitaladvertisingmanagement.campaignmanagement.domain.exception.DomainValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class CampaignExceptionHandler {
  @ExceptionHandler(DomainValidationException.class)
  public ResponseEntity<String> handleDomainValidation(DomainValidationException exception) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(exception.getMessage());
  }

  @ExceptionHandler(CampaignGroupNotFoundException.class)
  public ResponseEntity<String> handleCampaignGroupNotFound(
      CampaignGroupNotFoundException exception) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(exception.getMessage());
  }
}
