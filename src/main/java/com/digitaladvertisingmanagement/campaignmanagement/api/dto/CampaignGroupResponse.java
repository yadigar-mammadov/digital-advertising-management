package com.digitaladvertisingmanagement.campaignmanagement.api.dto;

import com.digitaladvertisingmanagement.campaignmanagement.application.result.CampaignGroupDetailsResult;
import java.util.List;

public record CampaignGroupResponse(
    Long id,
    Long ownerId,
    String name,
    com.digitaladvertisingmanagement.campaignmanagement.domain.model.enums.CampaignGroupStatus
        status,
    CampaignConfigurationResponse configuration,
    List<CampaignResponse> campaigns) {
  public static CampaignGroupResponse from(CampaignGroupDetailsResult result) {
    return new CampaignGroupResponse(
        result.id(),
        result.ownerId(),
        result.name(),
        result.status(),
        CampaignConfigurationResponse.from(result.configuration()),
        result.campaigns().stream().map(CampaignResponse::from).toList());
  }
}
