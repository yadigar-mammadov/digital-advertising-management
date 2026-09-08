package com.digitaladvertisingmanagement.campaignmanagement.api.dto;

import com.digitaladvertisingmanagement.campaignmanagement.application.result.CampaignGroupSummaryResult;
import com.digitaladvertisingmanagement.campaignmanagement.domain.model.enums.CampaignGroupStatus;

public record CampaignGroupListResponse(
    Long id, Long ownerId, String name, CampaignGroupStatus status) {

  public static CampaignGroupListResponse from(CampaignGroupSummaryResult campaignGroup) {
    return new CampaignGroupListResponse(
        campaignGroup.id(), campaignGroup.ownerId(), campaignGroup.name(), campaignGroup.status());
  }
}
