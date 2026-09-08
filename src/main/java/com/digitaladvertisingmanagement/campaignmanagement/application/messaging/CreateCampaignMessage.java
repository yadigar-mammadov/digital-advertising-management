package com.digitaladvertisingmanagement.campaignmanagement.application.messaging;

import com.digitaladvertisingmanagement.campaignmanagement.domain.model.enums.AdPlatform;

public record CreateCampaignMessage(
    Long campaignId, Long campaignGroupId, Long ownerId, AdPlatform platform, Long amount) {}
