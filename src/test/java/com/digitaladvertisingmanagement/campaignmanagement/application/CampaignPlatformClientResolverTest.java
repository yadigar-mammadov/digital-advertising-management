package com.digitaladvertisingmanagement.campaignmanagement.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.digitaladvertisingmanagement.campaignmanagement.application.port.CampaignPlatformClient;
import com.digitaladvertisingmanagement.campaignmanagement.application.service.CampaignPlatformClientResolver;
import com.digitaladvertisingmanagement.campaignmanagement.domain.model.enums.AdPlatform;
import java.util.List;
import org.junit.jupiter.api.Test;

public class CampaignPlatformClientResolverTest {

  @Test
  void shouldResolveClient() {
    CampaignPlatformClient googleClient = mock(CampaignPlatformClient.class);
    CampaignPlatformClient metaClient = mock(CampaignPlatformClient.class);

    when(googleClient.platform()).thenReturn(AdPlatform.GOOGLE);
    when(metaClient.platform()).thenReturn(AdPlatform.META);

    CampaignPlatformClientResolver resolver =
        new CampaignPlatformClientResolver(List.of(googleClient, metaClient));

    CampaignPlatformClient result = resolver.resolve(AdPlatform.GOOGLE);

    assertSame(googleClient, result);
  }

  @Test
  void shouldThrowExceptionWhenPlatformIsUnsupported() {
    CampaignPlatformClient googleClient = mock(CampaignPlatformClient.class);

    when(googleClient.platform()).thenReturn(AdPlatform.GOOGLE);

    CampaignPlatformClientResolver resolver =
        new CampaignPlatformClientResolver(List.of(googleClient));

    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> resolver.resolve(AdPlatform.META));

    assertEquals("Unsupported platform: " + AdPlatform.META, exception.getMessage());
  }
}
