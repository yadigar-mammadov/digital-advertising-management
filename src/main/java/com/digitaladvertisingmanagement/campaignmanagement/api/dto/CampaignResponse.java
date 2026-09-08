package com.digitaladvertisingmanagement.campaignmanagement.api.dto;

import com.digitaladvertisingmanagement.campaignmanagement.application.result.CampaignResult;
import com.digitaladvertisingmanagement.campaignmanagement.domain.model.enums.AdPlatform;
import com.digitaladvertisingmanagement.campaignmanagement.domain.model.enums.CampaignStatus;

// TODO add to documentation why you decided to leave AdPlatform and CampaignStatus here not using
// Api response.
/*Domain enums such as CampaignStatus and AdPlatform are reused in the API
because they currently represent the same stable business vocabulary;
separate API types will be introduced only when the external contract needs to evolve independently
from the domain model.
* */
public record CampaignResponse(
    Long id,
    AdPlatform platform,
    String name,
    CampaignStatus status,
    String externalCampaignId,
    String failureReason) {
  public static CampaignResponse from(CampaignResult campaign) {
    return new CampaignResponse(
        campaign.id(),
        campaign.platform(),
        campaign.name(),
        campaign.status(),
        campaign.externalCampaignId(),
        campaign.failureReason());
  }
}
