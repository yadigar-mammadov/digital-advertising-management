package com.digitaladvertisingmanagement.campaignmanagement.application.service;

import com.digitaladvertisingmanagement.campaignmanagement.application.command.CreateCampaignGroupCommand;
import com.digitaladvertisingmanagement.campaignmanagement.application.exception.CampaignGroupNotFoundException;
import com.digitaladvertisingmanagement.campaignmanagement.application.messaging.CreateCampaignMessage;
import com.digitaladvertisingmanagement.campaignmanagement.application.port.OutboxPayloadSerializer;
import com.digitaladvertisingmanagement.campaignmanagement.application.result.CampaignConfigurationResult;
import com.digitaladvertisingmanagement.campaignmanagement.application.result.CampaignGroupDetailsResult;
import com.digitaladvertisingmanagement.campaignmanagement.application.result.CampaignGroupSummaryResult;
import com.digitaladvertisingmanagement.campaignmanagement.application.result.CampaignResult;
import com.digitaladvertisingmanagement.campaignmanagement.domain.model.Campaign;
import com.digitaladvertisingmanagement.campaignmanagement.domain.model.CampaignConfiguration;
import com.digitaladvertisingmanagement.campaignmanagement.domain.model.CampaignGroup;
import com.digitaladvertisingmanagement.campaignmanagement.domain.model.OutboxEvent;
import com.digitaladvertisingmanagement.campaignmanagement.domain.repository.CampaignGroupRepository;
import com.digitaladvertisingmanagement.campaignmanagement.domain.repository.CampaignRepository;
import com.digitaladvertisingmanagement.campaignmanagement.domain.repository.OutboxEventRepository;
import com.digitaladvertisingmanagement.shared.pagination.PageRequestData;
import com.digitaladvertisingmanagement.shared.pagination.PageResult;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CampaignGroupService {
  private final CampaignGroupRepository campaignGroupRepository;
  private final CampaignRepository campaignRepository;
  private final OutboxEventRepository outboxEventRepository;
  private final OutboxPayloadSerializer outboxPayloadSerializer;
  public static final String CAMPAIGN_CREATION_REQUESTED = "CampaignCreationRequested";

  public CampaignGroupService(
      CampaignGroupRepository campaignGroupRepository,
      CampaignRepository campaignRepository,
      OutboxEventRepository outboxEventRepository,
      OutboxPayloadSerializer outboxPayloadSerializer) {
    this.campaignGroupRepository = campaignGroupRepository;
    this.campaignRepository = campaignRepository;
    this.outboxEventRepository = outboxEventRepository;
    this.outboxPayloadSerializer = outboxPayloadSerializer;
  }

  @Transactional
  public CampaignGroupDetailsResult create(CreateCampaignGroupCommand command) {
    CampaignGroup campaignGroup = CampaignGroup.create(command.ownerId(), command.name());

    campaignGroup.configure(
        command.objective(),
        command.budgetType(),
        command.budgetAmount(),
        command.startDate(),
        command.endDate());

    CampaignGroup savedCampaignGroup = campaignGroupRepository.save(campaignGroup);
    Long campaignGroupId = savedCampaignGroup.getId();

    List<Campaign> campaigns =
        command.platforms().stream()
            .map(
                platform ->
                    Campaign.create(campaignGroupId, command.ownerId(), platform, command.name()))
            .toList();

    List<Campaign> savedCampaigns = campaignRepository.saveALl(campaigns);

    List<OutboxEvent> outboxEvents =
        savedCampaigns.stream()
            .map(
                campaign -> {
                  CreateCampaignMessage message =
                      new CreateCampaignMessage(
                          campaign.getId(),
                          campaignGroupId,
                          campaign.getOwnerId(),
                          campaign.getPlatform(),
                          campaignGroup.getConfiguration().getBudgetAmount().longValue());

                  return OutboxEvent.create(
                      campaign.getId(),
                      CAMPAIGN_CREATION_REQUESTED,
                      outboxPayloadSerializer.serialize(message));
                })
            .toList();

    outboxEventRepository.saveAll(outboxEvents);

    return new CampaignGroupDetailsResult(
        savedCampaignGroup.getId(),
        savedCampaignGroup.getOwnerId(),
        savedCampaignGroup.getName(),
        savedCampaignGroup.getStatus(),
        toResult(savedCampaignGroup.getConfiguration()),
        campaigns.stream().map(CampaignGroupService::toResult).toList());
  }

  public PageResult<CampaignGroupSummaryResult> list(Long ownerId, PageRequestData pageRequest) {
    return campaignGroupRepository
        .findByOwnerId(ownerId, pageRequest)
        .map(CampaignGroupService::toResult);
  }

  public CampaignGroupDetailsResult get(Long ownerId, Long id) {
    CampaignGroup campaignGroup =
        campaignGroupRepository
            .findByIdAndOwnerId(id, ownerId)
            .orElseThrow(() -> new CampaignGroupNotFoundException(id, ownerId));

    List<Campaign> campaigns = campaignRepository.findByCampaignGroupId(campaignGroup.getId());

    return new CampaignGroupDetailsResult(
        campaignGroup.getId(),
        campaignGroup.getOwnerId(),
        campaignGroup.getName(),
        campaignGroup.getStatus(),
        toResult(campaignGroup.getConfiguration()),
        campaigns.stream().map(CampaignGroupService::toResult).toList());
  }

  private static CampaignGroupSummaryResult toResult(CampaignGroup campaignGroup) {
    return new CampaignGroupSummaryResult(
        campaignGroup.getId(),
        campaignGroup.getOwnerId(),
        campaignGroup.getName(),
        campaignGroup.getStatus());
  }

  private static CampaignConfigurationResult toResult(CampaignConfiguration configuration) {
    return new CampaignConfigurationResult(
        configuration.getId(),
        configuration.getObjective(),
        configuration.getBudgetType(),
        configuration.getBudgetAmount(),
        configuration.getStartDate(),
        configuration.getEndDate());
  }

  private static CampaignResult toResult(Campaign campaign) {
    return new CampaignResult(
        campaign.getId(),
        campaign.getPlatform(),
        campaign.getName(),
        campaign.getStatus(),
        campaign.getExternalCampaignId(),
        campaign.getFailureReason());
  }
}
