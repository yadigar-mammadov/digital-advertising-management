package com.digitaladvertisingmanagement.campaignmanagement.api.dto;

import com.digitaladvertisingmanagement.campaignmanagement.domain.model.enums.AdPlatform;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.Set;

public record CreateCampaignGroupRequest(
    @NotBlank String name,
    @NotEmpty @Size(max = 2) Set<AdPlatform> platforms,
    @Valid @NotNull CampaignConfigurationRequest configuration) {}
