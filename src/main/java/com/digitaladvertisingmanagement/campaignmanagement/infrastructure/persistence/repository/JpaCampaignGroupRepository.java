package com.digitaladvertisingmanagement.campaignmanagement.infrastructure.persistence.repository;

import com.digitaladvertisingmanagement.campaignmanagement.domain.model.CampaignConfiguration;
import com.digitaladvertisingmanagement.campaignmanagement.domain.model.CampaignGroup;
import com.digitaladvertisingmanagement.campaignmanagement.domain.repository.CampaignGroupRepository;
import com.digitaladvertisingmanagement.campaignmanagement.infrastructure.persistence.entity.CampaignConfigurationJpaEntity;
import com.digitaladvertisingmanagement.campaignmanagement.infrastructure.persistence.entity.CampaignGroupJpaEntity;
import com.digitaladvertisingmanagement.shared.pagination.PageRequestData;
import com.digitaladvertisingmanagement.shared.pagination.PageResult;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

@Repository
class JpaCampaignGroupRepository implements CampaignGroupRepository {

  private final SpringDataCampaignGroupJpaRepository repository;
  private final SpringDataCampaignConfigurationJpaRepository configurationRepository;

  JpaCampaignGroupRepository(
      SpringDataCampaignGroupJpaRepository repository,
      SpringDataCampaignConfigurationJpaRepository configurationRepository) {
    this.repository = repository;
    this.configurationRepository = configurationRepository;
  }

  @Override
  public CampaignGroup save(CampaignGroup group) {
    CampaignGroupJpaEntity saved = repository.save(toJpa(group));
    CampaignGroup savedGroup = toDomain(saved);

    CampaignConfiguration configuration = group.getConfiguration();

    if (configuration != null) {
      configuration.setCampaignGroupId(savedGroup.getId());
      CampaignConfigurationJpaEntity savedConfiguration =
          configurationRepository.save(
              toJpa(configuration, savedGroup.getId(), savedGroup.getOwnerId()));
      savedGroup.attachConfiguration(toDomain(savedConfiguration));
    }

    return savedGroup;
  }

  @Override
  public PageResult<CampaignGroup> findByOwnerId(Long id, PageRequestData pageRequest) {
    Pageable pageable = PageRequest.of(pageRequest.page(), pageRequest.size());

    Page<CampaignGroupJpaEntity> page = repository.findByOwnerId(id, pageable);

    return new PageResult<>(
        page.getContent().stream().map(JpaCampaignGroupRepository::toDomain).toList(),
        page.getTotalElements(),
        page.getTotalPages(),
        page.getNumber(),
        page.getSize());
  }

  @Override
  public Optional<CampaignGroup> findByIdAndOwnerId(Long id, Long ownerId) {
    return repository
        .findByIdAndOwnerId(id, ownerId)
        .map(JpaCampaignGroupRepository::toDomain)
        .map(
            group -> {
              CampaignConfigurationJpaEntity configuration =
                  configurationRepository
                      .findByCampaignGroupId(group.getId())
                      .orElseThrow(
                          () -> new IllegalStateException("Campaign configuration not found"));
              group.attachConfiguration(toDomain(configuration));
              return group;
            });
  }

  private static CampaignGroupJpaEntity toJpa(CampaignGroup group) {
    return new CampaignGroupJpaEntity(
        group.getId(), group.getOwnerId(), group.getName(), group.getStatus());
  }

  private static CampaignGroup toDomain(CampaignGroupJpaEntity entity) {
    return new CampaignGroup(
        entity.getId(), entity.getOwnerId(), entity.getName(), entity.getStatus());
  }

  private static CampaignConfigurationJpaEntity toJpa(
      CampaignConfiguration configuration, Long campaignGroupId, Long ownerId) {
    return new CampaignConfigurationJpaEntity(
        configuration.getId(),
        campaignGroupId,
        ownerId,
        configuration.getObjective(),
        configuration.getBudgetType(),
        configuration.getBudgetAmount(),
        configuration.getStartDate(),
        configuration.getEndDate());
  }

  private static CampaignConfiguration toDomain(CampaignConfigurationJpaEntity entity) {
    return new CampaignConfiguration(
        entity.getId(),
        entity.getCampaignGroupId(),
        entity.getOwnerId(),
        entity.getObjective(),
        entity.getBudgetType(),
        entity.getBudgetAmount(),
        entity.getStartDate(),
        entity.getEndDate(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }
}
