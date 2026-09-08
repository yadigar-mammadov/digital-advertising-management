package com.digitaladvertisingmanagement.campaignmanagement.api.dto;

import com.digitaladvertisingmanagement.campaignmanagement.application.result.CampaignConfigurationResult;
import com.digitaladvertisingmanagement.campaignmanagement.domain.model.enums.BudgetType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CampaignConfigurationResponse(
    Long id,
    String objective,
    BudgetType budgetType,
    BigDecimal budgetAmount,
    LocalDateTime startDate,
    LocalDateTime endDate) {
  public static CampaignConfigurationResponse from(CampaignConfigurationResult configuration) {
    return new CampaignConfigurationResponse(
        configuration.id(),
        configuration.objective(),
        configuration.budgetType(),
        configuration.budgetAmount(),
        configuration.startDate(),
        configuration.endDate());
  }
}
