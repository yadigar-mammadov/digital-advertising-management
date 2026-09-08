package com.digitaladvertisingmanagement.campaignmanagement.infrastructure.integration;

import com.digitaladvertisingmanagement.campaignmanagement.application.messaging.CreateCampaignMessage;
import com.digitaladvertisingmanagement.campaignmanagement.application.port.CampaignPlatformClient;
import com.digitaladvertisingmanagement.campaignmanagement.domain.model.enums.AdPlatform;
import com.digitaladvertisingmanagement.config.MetaAdsProperties;
import com.facebook.ads.sdk.APIContext;
import com.facebook.ads.sdk.APIException;
import com.facebook.ads.sdk.AdAccount;
import com.facebook.ads.sdk.Campaign;
import org.springframework.stereotype.Component;

@Component
public class MetaAdsCampaignClient implements CampaignPlatformClient {
  private final MetaAdsProperties metaAdsProperties;

  public MetaAdsCampaignClient(MetaAdsProperties metaAdsProperties) {
    this.metaAdsProperties = metaAdsProperties;
  }

  @Override
  public AdPlatform platform() {
    return AdPlatform.META;
  }

  @Override
  public String createCampaign(CreateCampaignMessage message) {
    APIContext context =
        new APIContext(metaAdsProperties.getAccessToken(), metaAdsProperties.getAppSecret());

    AdAccount account = new AdAccount(metaAdsProperties.getAdAccountId(), context);

    try {
      Campaign campaign =
          account
              .createCampaign()
              .setName("Campaign #" + message.campaignId())
              .setObjective(Campaign.EnumObjective.VALUE_LINK_CLICKS)
              .setStatus(Campaign.EnumStatus.VALUE_PAUSED)
              .execute();

      return campaign.getFieldId();

    } catch (APIException e) {
      throw new IllegalStateException("Failed to create Meta campaign", e);
    }
  }
}
