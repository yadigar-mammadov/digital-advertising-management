package com.digitaladvertisingmanagement.campaignmanagement.application.command;

import com.digitaladvertisingmanagement.campaignmanagement.domain.model.enums.AdPlatform;
import com.digitaladvertisingmanagement.campaignmanagement.domain.model.enums.BudgetType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

public record CreateCampaignGroupCommand(
    Long ownerId,
    String name,
    Set<AdPlatform> platforms,
    String objective,
    BudgetType budgetType,
    BigDecimal budgetAmount,
    LocalDateTime startDate,
    LocalDateTime endDate) {}
