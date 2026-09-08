package com.digitaladvertisingmanagement.campaignmanagement.application.result;

import com.digitaladvertisingmanagement.campaignmanagement.domain.model.enums.CampaignGroupStatus;
import java.util.List;

public record CampaignGroupDetailsResult(
    Long id,
    Long ownerId,
    String name,
    CampaignGroupStatus status,
    CampaignConfigurationResult configuration,
    List<CampaignResult> campaigns) {}
