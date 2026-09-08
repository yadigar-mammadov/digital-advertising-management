package com.digitaladvertisingmanagement.campaignmanagement.infrastructure.persistence.repository;

import com.digitaladvertisingmanagement.campaignmanagement.infrastructure.persistence.entity.CampaignConfigurationJpaEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataCampaignConfigurationJpaRepository
    extends JpaRepository<CampaignConfigurationJpaEntity, Long> {
  Optional<CampaignConfigurationJpaEntity> findByCampaignGroupId(Long campaignGroupId);
}
