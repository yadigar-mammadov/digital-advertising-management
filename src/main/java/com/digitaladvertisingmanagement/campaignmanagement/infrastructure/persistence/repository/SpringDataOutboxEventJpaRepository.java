package com.digitaladvertisingmanagement.campaignmanagement.infrastructure.persistence.repository;

import com.digitaladvertisingmanagement.campaignmanagement.infrastructure.persistence.entity.OutboxEventJpaEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

interface SpringDataOutboxEventJpaRepository extends JpaRepository<OutboxEventJpaEntity, UUID> {
  List<OutboxEventJpaEntity> findByPublishedAtIsNullOrderByCreatedAtAsc(Pageable pageable);
}
