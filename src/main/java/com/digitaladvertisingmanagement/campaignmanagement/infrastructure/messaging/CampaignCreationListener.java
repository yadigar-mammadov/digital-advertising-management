package com.digitaladvertisingmanagement.campaignmanagement.infrastructure.messaging;

import com.digitaladvertisingmanagement.campaignmanagement.application.messaging.CreateCampaignMessage;
import com.digitaladvertisingmanagement.campaignmanagement.application.service.CampaignCreationService;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Component
public class CampaignCreationListener {
  private final JsonMapper jsonMapper;
  private final CampaignCreationService campaignCreationService;

  public CampaignCreationListener(
      JsonMapper jsonMapper, CampaignCreationService campaignCreationService) {
    this.jsonMapper = jsonMapper;
    this.campaignCreationService = campaignCreationService;
  }

  @RetryableTopic(
      attempts = "4",
      backOff = @BackOff(delay = 2000, multiplier = 2, maxDelay = 10000))
  @KafkaListener(topics = "campaign-create", groupId = "campaign-creation", concurrency = "5")
  public void consume(String payload) {
    CreateCampaignMessage message = jsonMapper.readValue(payload, CreateCampaignMessage.class);

    campaignCreationService.create(message);
  }
}
