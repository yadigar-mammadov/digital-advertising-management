package com.digitaladvertisingmanagement.campaignmanagement.infrastructure.persistence.repository;

import com.digitaladvertisingmanagement.campaignmanagement.domain.model.OutboxEvent;
import com.digitaladvertisingmanagement.campaignmanagement.domain.repository.OutboxEventRepository;
import com.digitaladvertisingmanagement.campaignmanagement.infrastructure.persistence.entity.OutboxEventJpaEntity;
import java.util.List;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
class JpaOutboxEventRepository implements OutboxEventRepository {

  private final SpringDataOutboxEventJpaRepository repository;

  public JpaOutboxEventRepository(SpringDataOutboxEventJpaRepository repository) {
    this.repository = repository;
  }

  @Override
  public OutboxEvent save(OutboxEvent outboxEvent) {
    OutboxEventJpaEntity savedEntity = repository.save(toJpa(outboxEvent));
    return toDomain(savedEntity);
  }

  @Override
  public List<OutboxEvent> findUnpublished(int limit) {
    return repository.findByPublishedAtIsNullOrderByCreatedAtAsc(PageRequest.of(0, limit)).stream()
        .map(JpaOutboxEventRepository::toDomain)
        .toList();
  }

  @Override
  public void saveAll(List<OutboxEvent> events) {
    List<OutboxEventJpaEntity> entities =
        events.stream().map(JpaOutboxEventRepository::toJpa).toList();

    repository.saveAll(entities);
  }

  private static OutboxEventJpaEntity toJpa(OutboxEvent event) {
    return new OutboxEventJpaEntity(
        event.getId(),
        event.getAggregateId(),
        event.getEventType(),
        event.getPayload(),
        event.getCreatedAt(),
        event.getPublishedAt());
  }

  private static OutboxEvent toDomain(OutboxEventJpaEntity entity) {
    return OutboxEvent.restore(
        entity.getId(),
        entity.getAggregateId(),
        entity.getEventType(),
        entity.getPayload(),
        entity.getCreatedAt(),
        entity.getPublishedAt());
  }
}
