package com.digitaladvertisingmanagement.campaignmanagement.application.result;

import com.digitaladvertisingmanagement.campaignmanagement.domain.model.enums.CampaignGroupStatus;

public record CampaignGroupSummaryResult(
    Long id, Long ownerId, String name, CampaignGroupStatus status) {}
