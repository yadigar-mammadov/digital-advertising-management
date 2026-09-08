package com.digitaladvertisingmanagement.campaignmanagement.infrastructure.integration;

import static com.google.ads.googleads.v25.enums.EuPoliticalAdvertisingStatusEnum.EuPoliticalAdvertisingStatus.DOES_NOT_CONTAIN_EU_POLITICAL_ADVERTISING;

import com.digitaladvertisingmanagement.campaignmanagement.application.messaging.CreateCampaignMessage;
import com.digitaladvertisingmanagement.campaignmanagement.application.port.CampaignPlatformClient;
import com.digitaladvertisingmanagement.campaignmanagement.domain.model.enums.AdPlatform;
import com.digitaladvertisingmanagement.config.GoogleAdsProperties;
import com.google.ads.googleads.lib.GoogleAdsClient;
import com.google.ads.googleads.v25.common.ManualCpc;
import com.google.ads.googleads.v25.enums.AdvertisingChannelTypeEnum.AdvertisingChannelType;
import com.google.ads.googleads.v25.enums.BudgetDeliveryMethodEnum.BudgetDeliveryMethod;
import com.google.ads.googleads.v25.enums.CampaignStatusEnum.CampaignStatus;
import com.google.ads.googleads.v25.resources.Campaign;
import com.google.ads.googleads.v25.resources.CampaignBudget;
import com.google.ads.googleads.v25.services.CampaignBudgetOperation;
import com.google.ads.googleads.v25.services.CampaignBudgetServiceClient;
import com.google.ads.googleads.v25.services.CampaignOperation;
import com.google.ads.googleads.v25.services.CampaignServiceClient;
import com.google.ads.googleads.v25.services.MutateCampaignBudgetsResponse;
import com.google.ads.googleads.v25.services.MutateCampaignsResponse;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "google.ads.enabled", havingValue = "true")
public class GoogleAdsCampaignClient implements CampaignPlatformClient {

  private final GoogleAdsClient googleAdsClient;
  private final GoogleAdsProperties googleAdsProperties;

  public GoogleAdsCampaignClient(
      GoogleAdsClient googleAdsClient, GoogleAdsProperties googleAdsProperties) {
    this.googleAdsClient = googleAdsClient;
    this.googleAdsProperties = googleAdsProperties;
  }

  @Override
  public AdPlatform platform() {
    return AdPlatform.GOOGLE;
  }

  @Override
  public String createCampaign(CreateCampaignMessage message) {
    String customerId = googleAdsProperties.getCustomerId();

    String budgetResourceName = createBudget(message);

    Campaign campaign =
        Campaign.newBuilder()
            .setName("Campaign #" + message.campaignId())
            .setAdvertisingChannelType(AdvertisingChannelType.SEARCH)
            .setStatus(CampaignStatus.PAUSED)
            .setManualCpc(ManualCpc.newBuilder().build())
            .setCampaignBudget(budgetResourceName)
            .setContainsEuPoliticalAdvertising(DOES_NOT_CONTAIN_EU_POLITICAL_ADVERTISING)
            .build();

    CampaignOperation operation = CampaignOperation.newBuilder().setCreate(campaign).build();

    try (CampaignServiceClient client =
        googleAdsClient.getLatestVersion().createCampaignServiceClient()) {

      MutateCampaignsResponse response = client.mutateCampaigns(customerId, List.of(operation));

      String resourceName = response.getResults(0).getResourceName();

      return resourceName.substring(resourceName.lastIndexOf('/') + 1);
    }
  }

  private String createBudget(CreateCampaignMessage message) {
    CampaignBudget budget =
        CampaignBudget.newBuilder()
            .setName("Budget #" + message.campaignId())
            .setAmountMicros(message.amount())
            .setDeliveryMethod(BudgetDeliveryMethod.STANDARD)
            .build();

    CampaignBudgetOperation operation =
        CampaignBudgetOperation.newBuilder().setCreate(budget).build();

    try (CampaignBudgetServiceClient client =
        googleAdsClient.getLatestVersion().createCampaignBudgetServiceClient()) {

      MutateCampaignBudgetsResponse response =
          client.mutateCampaignBudgets(googleAdsProperties.getCustomerId(), List.of(operation));

      return response.getResults(0).getResourceName();
    }
  }
}
