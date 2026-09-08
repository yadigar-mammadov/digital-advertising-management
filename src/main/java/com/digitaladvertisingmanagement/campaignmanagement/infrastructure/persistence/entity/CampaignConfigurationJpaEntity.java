package com.digitaladvertisingmanagement.campaignmanagement.infrastructure.persistence.entity;

import com.digitaladvertisingmanagement.campaignmanagement.domain.model.enums.BudgetType;
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
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "campaign_configurations")
public class CampaignConfigurationJpaEntity {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long campaignGroupId;
  private Long ownerId;
  private String objective;

  @Enumerated(EnumType.STRING)
  private BudgetType budgetType;

  private BigDecimal budgetAmount;
  private LocalDateTime startDate;
  private LocalDateTime endDate;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  protected CampaignConfigurationJpaEntity() {}

  public CampaignConfigurationJpaEntity(
      Long id,
      Long campaignGroupId,
      Long ownerId,
      String objective,
      BudgetType budgetType,
      BigDecimal budgetAmount,
      LocalDateTime startDate,
      LocalDateTime endDate) {
    this.id = id;
    this.campaignGroupId = campaignGroupId;
    this.ownerId = ownerId;
    this.objective = objective;
    this.budgetType = budgetType;
    this.budgetAmount = budgetAmount;
    this.startDate = startDate;
    this.endDate = endDate;
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

  public String getObjective() {
    return objective;
  }

  public BudgetType getBudgetType() {
    return budgetType;
  }

  public BigDecimal getBudgetAmount() {
    return budgetAmount;
  }

  public LocalDateTime getStartDate() {
    return startDate;
  }

  public LocalDateTime getEndDate() {
    return endDate;
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
