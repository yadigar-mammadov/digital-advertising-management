package com.digitaladvertisingmanagement.campaignmanagement.application.service;

import com.digitaladvertisingmanagement.campaignmanagement.application.messaging.CreateCampaignMessage;
import com.digitaladvertisingmanagement.campaignmanagement.application.port.CampaignPlatformClient;
import com.digitaladvertisingmanagement.campaignmanagement.domain.model.Campaign;
import com.digitaladvertisingmanagement.campaignmanagement.domain.repository.CampaignRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CampaignCreationService {
  private static final Logger log = LoggerFactory.getLogger(CampaignCreationService.class);
  private final CampaignPlatformClientResolver campaignPlatformClientResolver;
  private final CampaignRepository campaignRepository;

  public CampaignCreationService(
      CampaignPlatformClientResolver campaignPlatformClientResolver,
      CampaignRepository campaignRepository) {
    this.campaignPlatformClientResolver = campaignPlatformClientResolver;
    this.campaignRepository = campaignRepository;
  }

  public void create(CreateCampaignMessage message) {
    boolean claimed = campaignRepository.tryMarkProcessing(message.campaignId());

    if (!claimed) {
      log.info("Campaign already processing or created. campaignId={}", message.campaignId());
      return;
    }

    String externalCampaignId = null;

    try {

      Campaign campaign =
          campaignRepository
              .findById(message.campaignId())
              .orElseThrow(() -> new IllegalArgumentException("Campaign not found"));

      externalCampaignId = campaign.getExternalCampaignId();

      if (externalCampaignId != null) {
        campaignRepository.markCreated(campaign.getId(), campaign.getExternalCampaignId());

        log.info(
            "Campaign reconciled using existing external id. campaignId={}, externalCampaignId={}",
            campaign.getId(),
            campaign.getExternalCampaignId());

        return;
      }

      CampaignPlatformClient client = campaignPlatformClientResolver.resolve(message.platform());
      externalCampaignId = client.createCampaign(message);
      campaignRepository.markCreated(message.campaignId(), externalCampaignId);
    } catch (Exception e) {
      campaignRepository.markFailed(message.campaignId(), getFailureReason(e), externalCampaignId);

      throw e;
    }
  }

  private String getFailureReason(Exception exception) {
    String message = exception.getMessage();

    if (message == null) {
      return exception.getClass().getSimpleName();
    }

    return message.length() > 1000 ? message.substring(0, 1000) : message;
  }
}
