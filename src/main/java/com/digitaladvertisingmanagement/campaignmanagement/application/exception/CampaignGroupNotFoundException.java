package com.digitaladvertisingmanagement.campaignmanagement.application.exception;

public class CampaignGroupNotFoundException extends RuntimeException {
  public CampaignGroupNotFoundException(Long id, Long ownerId) {
    super("Campaign group not found. id=" + id + ", ownerId=" + ownerId);
  }
}
