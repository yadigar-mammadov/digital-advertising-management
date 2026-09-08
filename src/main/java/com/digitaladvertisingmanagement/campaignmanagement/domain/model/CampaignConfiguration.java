package com.digitaladvertisingmanagement.campaignmanagement.domain.model;

import com.digitaladvertisingmanagement.campaignmanagement.domain.exception.DomainValidationException;
import com.digitaladvertisingmanagement.campaignmanagement.domain.model.enums.BudgetType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CampaignConfiguration {
  private Long id;
  private Long campaignGroupId;
  private Long ownerId;
  private String objective;
  private BudgetType budgetType;
  private BigDecimal budgetAmount;
  private LocalDateTime startDate;
  private LocalDateTime endDate;
  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;

  public CampaignConfiguration(
      Long id,
      Long campaignGroupId,
      Long ownerId,
      String objective,
      BudgetType budgetType,
      BigDecimal budgetAmount,
      LocalDateTime startDate,
      LocalDateTime endDate,
      LocalDateTime createdAt,
      LocalDateTime updatedAt) {
    this.id = id;
    this.campaignGroupId = campaignGroupId;
    this.ownerId = ownerId;
    this.objective = objective;
    this.budgetType = budgetType;
    this.budgetAmount = budgetAmount;
    this.startDate = startDate;
    this.endDate = endDate;
    this.createdAt = createdAt;
    this.updatedAt = updatedAt;
  }

  public static CampaignConfiguration create(
      Long campaignGroupId,
      Long ownerId,
      String objective,
      BudgetType budgetType,
      BigDecimal budgetAmount,
      LocalDateTime startDate,
      LocalDateTime endDate) {
    if (ownerId == null) {
      throw new DomainValidationException("Owner id is required.");
    }

    if (objective == null || objective.isBlank()) {
      throw new DomainValidationException("Objective is required.");
    }

    if (budgetType == null) {
      throw new DomainValidationException("Budget type is required.");
    }

    if (budgetAmount == null || budgetAmount.signum() <= 0) {
      throw new DomainValidationException("Budget amount must be positive.");
    }

    return new CampaignConfiguration(
        null,
        campaignGroupId,
        ownerId,
        objective,
        budgetType,
        budgetAmount,
        startDate,
        endDate,
        null,
        null);
  }

  public Long getId() {
    return id;
  }

  public Long getCampaignGroupId() {
    return campaignGroupId;
  }

  public void setCampaignGroupId(Long campaignGroupId) {
    this.campaignGroupId = campaignGroupId;
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
}
