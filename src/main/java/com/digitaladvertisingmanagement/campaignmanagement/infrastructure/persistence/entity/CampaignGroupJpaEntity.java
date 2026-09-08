package com.digitaladvertisingmanagement.campaignmanagement.infrastructure.persistence.entity;

import com.digitaladvertisingmanagement.campaignmanagement.domain.model.enums.CampaignGroupStatus;
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
@Table(name = "campaign_groups")
public class CampaignGroupJpaEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long ownerId;
  private String name;

  @Enumerated(EnumType.STRING)
  private CampaignGroupStatus status;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  protected CampaignGroupJpaEntity() {}

  public CampaignGroupJpaEntity(Long id, Long ownerId, String name, CampaignGroupStatus status) {
    this.id = id;
    this.ownerId = ownerId;
    this.name = name;
    this.status = status;
  }

  public Long getId() {
    return id;
  }

  public Long getOwnerId() {
    return ownerId;
  }

  public String getName() {
    return name;
  }

  public CampaignGroupStatus getStatus() {
    return status;
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
