package com.digitaladvertisingmanagement.campaignmanagement.domain.model;

import com.digitaladvertisingmanagement.campaignmanagement.domain.model.enums.AdPlatform;
import com.digitaladvertisingmanagement.campaignmanagement.domain.model.enums.CampaignStatus;
import java.time.LocalDateTime;

public class Campaign {
  private Long id;
  private Long campaignGroupId;
  private Long ownerId;
  private AdPlatform platform;
  private String name;
  private CampaignStatus status;
  private String externalCampaignId;
  private String failureReason;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public Campaign(
      Long id,
      Long campaignGroupId,
      Long ownerId,
      AdPlatform platform,
      String name,
      CampaignStatus status,
      String externalCampaignId,
      String failureReason,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {
    this.id = id;
    this.campaignGroupId = campaignGroupId;
    this.ownerId = ownerId;
    this.platform = platform;
    this.name = name;
    this.status = status;
    this.externalCampaignId = externalCampaignId;
    this.failureReason = failureReason;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public static Campaign create(
      Long campaignGroupId, Long ownerId, AdPlatform platform, String name) {
    if (campaignGroupId == null) {
      throw new IllegalArgumentException("Campaign group id is required.");
    }

    if (ownerId == null) {
      throw new IllegalArgumentException("Owner id is required.");
    }

    if (platform == null) {
      throw new IllegalArgumentException("Platform is required.");
    }

    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Campaign name is required.");
    }

    return new Campaign(
        null,
        campaignGroupId,
        ownerId,
        platform,
        name,
        CampaignStatus.PENDING,
        null,
        null,
        null,
        null);
  }

  public Long getId() {
    return id;
  }

  public Long getCampaignGroupId() {
    return campaignGroupId;
  }

  public Long getOwnerId() {
    return ownerId;
  }

  public AdPlatform getPlatform() {
    return platform;
  }

  public String getName() {
    return name;
  }

  public CampaignStatus getStatus() {
    return status;
  }

  public String getExternalCampaignId() {
    return externalCampaignId;
  }

  public String getFailureReason() {
    return failureReason;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public boolean isCreated() {
    return status == CampaignStatus.CREATED;
  }

  public boolean isProcessing() {
    return status == CampaignStatus.PROCESSING;
  }
}
