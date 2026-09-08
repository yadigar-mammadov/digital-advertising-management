package com.digitaladvertisingmanagement.campaignmanagement.application.result;

import com.digitaladvertisingmanagement.campaignmanagement.domain.model.enums.BudgetType;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CampaignConfigurationResult(
    Long id,
    String objective,
    BudgetType budgetType,
    BigDecimal budgetAmount,
    LocalDateTime startDate,
    LocalDateTime endDate) {}
