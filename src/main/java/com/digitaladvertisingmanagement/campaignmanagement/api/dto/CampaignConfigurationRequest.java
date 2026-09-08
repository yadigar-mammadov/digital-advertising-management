package com.digitaladvertisingmanagement.campaignmanagement.api.dto;

import com.digitaladvertisingmanagement.campaignmanagement.domain.model.enums.BudgetType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public record CampaignConfigurationRequest(
    @NotBlank String objective,
    @NotNull BudgetType budgetType,
    @NotNull @Positive BigDecimal budgetAmount,
    LocalDateTime startDate,
    LocalDateTime endDate) {}
