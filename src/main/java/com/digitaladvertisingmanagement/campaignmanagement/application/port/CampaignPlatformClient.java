package com.digitaladvertisingmanagement.campaignmanagement.application.port;

import com.digitaladvertisingmanagement.campaignmanagement.application.messaging.CreateCampaignMessage;
import com.digitaladvertisingmanagement.campaignmanagement.domain.model.enums.AdPlatform;

public interface CampaignPlatformClient {
  AdPlatform platform();

  String createCampaign(CreateCampaignMessage message);
}
