package com.digitaladvertisingmanagement.campaignmanagement.application;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import com.digitaladvertisingmanagement.campaignmanagement.application.messaging.CreateCampaignMessage;
import com.digitaladvertisingmanagement.campaignmanagement.application.port.CampaignPlatformClient;
import com.digitaladvertisingmanagement.campaignmanagement.application.service.CampaignCreationService;
import com.digitaladvertisingmanagement.campaignmanagement.application.service.CampaignPlatformClientResolver;
import com.digitaladvertisingmanagement.campaignmanagement.domain.model.Campaign;
import com.digitaladvertisingmanagement.campaignmanagement.domain.model.enums.AdPlatform;
import com.digitaladvertisingmanagement.campaignmanagement.domain.repository.CampaignRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CampaignCreationServiceTest {
  @Mock private CampaignPlatformClientResolver resolver;

  @Mock private CampaignRepository campaignRepository;

  @Mock private CampaignPlatformClient client;

  @Mock private CreateCampaignMessage message;

  @Mock private Campaign campaign;

  @InjectMocks private CampaignCreationService service;

  @Test
  void shouldCreateCampaignOnExternalPlatform() {
    Long campaignId = 1L;
    AdPlatform platform = AdPlatform.GOOGLE;
    String externalId = "google-123";

    when(message.campaignId()).thenReturn(campaignId);
    when(message.platform()).thenReturn(platform);

    when(campaignRepository.tryMarkProcessing(campaignId)).thenReturn(true);

    when(campaignRepository.findById(campaignId)).thenReturn(Optional.of(campaign));

    when(campaign.getExternalCampaignId()).thenReturn(null);

    when(resolver.resolve(platform)).thenReturn(client);

    when(client.createCampaign(message)).thenReturn(externalId);

    service.create(message);

    verify(resolver).resolve(platform);
    verify(client).createCampaign(message);

    verify(campaignRepository).markCreated(campaignId, externalId);

    verify(campaignRepository, never()).markFailed(any(), any(), any());
  }

  @Test
  void shouldReconcileCampaignWhenExternalIdAlreadyExists() {
    Long campaignId = 1L;
    String externalId = "google-123";

    when(message.campaignId()).thenReturn(campaignId);
    when(campaignRepository.tryMarkProcessing(campaignId)).thenReturn(true);

    when(campaignRepository.findById(campaignId)).thenReturn(Optional.of(campaign));

    when(campaign.getId()).thenReturn(campaignId);
    when(campaign.getExternalCampaignId()).thenReturn(externalId);

    service.create(message);

    verify(campaignRepository).markCreated(campaignId, externalId);

    verifyNoInteractions(resolver);
    verifyNoInteractions(client);
  }

  @Test
  void shouldReturnWhenCampaignCannotBeClaimed() {
    Long campaignId = 1L;

    when(message.campaignId()).thenReturn(campaignId);
    when(campaignRepository.tryMarkProcessing(campaignId)).thenReturn(false);

    service.create(message);

    verify(campaignRepository).tryMarkProcessing(campaignId);

    verifyNoMoreInteractions(campaignRepository);
    verifyNoInteractions(resolver);
  }

  @Test
  void shouldMarkCampaignFailedWhenCampaignDoesNotExist() {
    Long campaignId = 1L;

    when(message.campaignId()).thenReturn(campaignId);

    when(campaignRepository.tryMarkProcessing(campaignId)).thenReturn(true);

    when(campaignRepository.findById(campaignId)).thenReturn(Optional.empty());

    assertThrows(IllegalArgumentException.class, () -> service.create(message));

    verify(campaignRepository).markFailed(campaignId, "Campaign not found", null);

    verifyNoInteractions(resolver);
  }

  @Test
  void shouldMarkCampaignFailedWhenExternalCreationFails() {
    Long campaignId = 1L;
    AdPlatform platform = AdPlatform.GOOGLE;

    RuntimeException exception = new RuntimeException("Google API failure");

    when(message.campaignId()).thenReturn(campaignId);
    when(message.platform()).thenReturn(platform);

    when(campaignRepository.tryMarkProcessing(campaignId)).thenReturn(true);

    when(campaignRepository.findById(campaignId)).thenReturn(Optional.of(campaign));

    when(campaign.getExternalCampaignId()).thenReturn(null);

    when(resolver.resolve(platform)).thenReturn(client);

    when(client.createCampaign(message)).thenThrow(exception);

    assertThrows(RuntimeException.class, () -> service.create(message));

    verify(campaignRepository).markFailed(campaignId, "Google API failure", null);

    verify(campaignRepository, never()).markCreated(any(), any());
  }
}
