package com.digitaladvertisingmanagement.campaignmanagement.application.service;

import com.digitaladvertisingmanagement.campaignmanagement.application.port.CampaignPlatformClient;
import com.digitaladvertisingmanagement.campaignmanagement.domain.model.enums.AdPlatform;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class CampaignPlatformClientResolver {
  private final Map<AdPlatform, CampaignPlatformClient> clients;

  public CampaignPlatformClientResolver(List<CampaignPlatformClient> clients) {
    this.clients =
        clients.stream()
            .collect(Collectors.toMap(CampaignPlatformClient::platform, Function.identity()));
  }

  public CampaignPlatformClient resolve(AdPlatform platform) {
    CampaignPlatformClient client = clients.get(platform);

    if (client == null) {
      throw new IllegalArgumentException("Unsupported platform: " + platform);
    }

    return client;
  }
}
