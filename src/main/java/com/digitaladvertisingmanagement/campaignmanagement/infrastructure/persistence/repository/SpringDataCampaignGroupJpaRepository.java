package com.digitaladvertisingmanagement.campaignmanagement.infrastructure.persistence.repository;

import com.digitaladvertisingmanagement.campaignmanagement.infrastructure.persistence.entity.CampaignGroupJpaEntity;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataCampaignGroupJpaRepository extends JpaRepository<CampaignGroupJpaEntity, Long> {

  Page<CampaignGroupJpaEntity> findByOwnerId(Long ownerId, Pageable pageable);

  Optional<CampaignGroupJpaEntity> findByIdAndOwnerId(Long id, Long ownerId);
}
