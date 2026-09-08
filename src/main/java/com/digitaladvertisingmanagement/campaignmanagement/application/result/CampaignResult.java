package com.digitaladvertisingmanagement.campaignmanagement.application.result;

import com.digitaladvertisingmanagement.campaignmanagement.domain.model.enums.AdPlatform;
import com.digitaladvertisingmanagement.campaignmanagement.domain.model.enums.CampaignStatus;

public record CampaignResult(
    Long id,
    AdPlatform platform,
    String name,
    CampaignStatus status,
    String externalCampaignId,
    String failureReason) {}
