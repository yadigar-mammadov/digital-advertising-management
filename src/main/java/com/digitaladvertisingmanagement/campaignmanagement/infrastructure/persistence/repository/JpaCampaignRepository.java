package com.digitaladvertisingmanagement.campaignmanagement.infrastructure.persistence.repository;

import com.digitaladvertisingmanagement.campaignmanagement.domain.model.Campaign;
import com.digitaladvertisingmanagement.campaignmanagement.domain.model.enums.CampaignStatus;
import com.digitaladvertisingmanagement.campaignmanagement.domain.repository.CampaignRepository;
import com.digitaladvertisingmanagement.campaignmanagement.infrastructure.persistence.entity.CampaignJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
class JpaCampaignRepository implements CampaignRepository {
  private final SpringDataCampaignJpaRepository repository;

  JpaCampaignRepository(SpringDataCampaignJpaRepository repository) {
    this.repository = repository;
  }

  @Override
  public Campaign save(Campaign campaign) {
    CampaignJpaEntity saved = repository.save(toJpa(campaign));
    return toDomain(saved);
  }

  @Override
  public List<Campaign> saveALl(List<Campaign> campaigns) {
    return repository
        .saveAll(campaigns.stream().map(JpaCampaignRepository::toJpa).toList())
        .stream()
        .map(JpaCampaignRepository::toDomain)
        .toList();
  }

  @Override
  public Optional<Campaign> findById(Long id) {
    return repository.findById(id).map(JpaCampaignRepository::toDomain);
  }

  @Override
  public List<Campaign> findByCampaignGroupId(Long campaignGroupId) {
    return repository.findByCampaignGroupId(campaignGroupId).stream()
        .map(JpaCampaignRepository::toDomain)
        .toList();
  }

  @Override
  @Transactional
  public boolean tryMarkProcessing(Long campaignId) {
    int updated =
        repository.tryMarkProcessing(
            campaignId, CampaignStatus.PROCESSING, CampaignStatus.PENDING, CampaignStatus.FAILED);

    return updated == 1;
  }

  @Override
  @Transactional
  public void markCreated(Long campaignId, String externalCampaignId) {
    int updated =
        repository.markCreated(
            campaignId, externalCampaignId, CampaignStatus.CREATED, CampaignStatus.PROCESSING);

    if (updated != 1) {
      throw new IllegalStateException("Failed to mark campaign as created: " + campaignId);
    }
  }

  @Override
  @Transactional
  public void markFailed(Long campaignId, String failureReason, String externalCampaignId) {
    int updated =
        repository.markFailed(
            campaignId,
            failureReason,
            externalCampaignId,
            CampaignStatus.FAILED,
            CampaignStatus.PROCESSING);

    if (updated != 1) {
      throw new IllegalStateException("Failed to mark campaign as failed: " + campaignId);
    }
  }

  private static CampaignJpaEntity toJpa(Campaign campaign) {
    return new CampaignJpaEntity(
        campaign.getId(),
        campaign.getCampaignGroupId(),
        campaign.getOwnerId(),
        campaign.getPlatform(),
        campaign.getName(),
        campaign.getStatus(),
        campaign.getExternalCampaignId(),
        campaign.getFailureReason());
  }

  private static Campaign toDomain(CampaignJpaEntity entity) {
    return new Campaign(
        entity.getId(),
        entity.getCampaignGroupId(),
        entity.getOwnerId(),
        entity.getPlatform(),
        entity.getName(),
        entity.getStatus(),
        entity.getExternalCampaignId(),
        entity.getFailureReason(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }
}
