package com.digitaladvertisingmanagement.campaignmanagement.domain.model;

import com.digitaladvertisingmanagement.campaignmanagement.domain.exception.DomainValidationException;
import com.digitaladvertisingmanagement.campaignmanagement.domain.model.enums.BudgetType;
import com.digitaladvertisingmanagement.campaignmanagement.domain.model.enums.CampaignGroupStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CampaignGroup {
  private Long id;
  private Long ownerId;
  private String name;
  private CampaignGroupStatus status;
  private CampaignConfiguration configuration;

  public CampaignGroup(Long id, Long ownerId, String name, CampaignGroupStatus status) {
    this.id = id;
    this.ownerId = ownerId;
    this.name = name;
    this.status = status;
  }

  public static CampaignGroup create(Long ownerId, String name) {
    if (ownerId == null) {
      throw new DomainValidationException("Owner id is required.");
    }

    if (name == null || name.isBlank()) {
      throw new DomainValidationException("Campaign group name is required.");
    }

    return new CampaignGroup(null, ownerId, name, CampaignGroupStatus.ACTIVE);
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

  public void makeArchived() {
    this.status = CampaignGroupStatus.ARCHIVED;
  }

  public void configure(
      String objective,
      BudgetType budgetType,
      BigDecimal budgetAmount,
      LocalDateTime startDate,
      LocalDateTime endDate) {
    if (ownerId == null) {
      throw new DomainValidationException("Campaign group must be created before configuration.");
    }

    this.configuration =
        CampaignConfiguration.create(
            id, ownerId, objective, budgetType, budgetAmount, startDate, endDate);
  }

  public CampaignConfiguration getConfiguration() {
    return configuration;
  }

  public void attachConfiguration(CampaignConfiguration configuration) {
    if (configuration != null && !configuration.getCampaignGroupId().equals(id)) {
      throw new DomainValidationException("Configuration does not belong to this campaign group.");
    }

    this.configuration = configuration;
  }
}
