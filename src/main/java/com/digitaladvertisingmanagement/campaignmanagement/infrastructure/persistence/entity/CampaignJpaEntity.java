package com.digitaladvertisingmanagement.campaignmanagement.infrastructure.persistence.entity;

import com.digitaladvertisingmanagement.campaignmanagement.domain.model.enums.AdPlatform;
import com.digitaladvertisingmanagement.campaignmanagement.domain.model.enums.CampaignStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "campaigns")
public class CampaignJpaEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long campaignGroupId;
  private Long ownerId;

  @Enumerated(EnumType.STRING)
  private AdPlatform platform;

  private String name;

  @Enumerated(EnumType.STRING)
  private CampaignStatus status;

  private String externalCampaignId;

  @Column(columnDefinition = "TEXT")
  private String failureReason;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  protected CampaignJpaEntity() {}

  public CampaignJpaEntity(
      Long id,
      Long campaignGroupId,
      Long ownerId,
      AdPlatform platform,
      String name,
      CampaignStatus status,
      String externalCampaignId,
      String failureReason) {
    this.id = id;
    this.campaignGroupId = campaignGroupId;
    this.ownerId = ownerId;
    this.platform = platform;
    this.name = name;
    this.status = status;
    this.externalCampaignId = externalCampaignId;
    this.failureReason = failureReason;
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

  @PrePersist
  void onCreate() {
    this.createdAt = LocalDateTime.now();
    this.updatedAt = LocalDateTime.now();
  }

  @PreUpdate
  void onUpdate() {
    this.updatedAt = LocalDateTime.now();
  }
}
