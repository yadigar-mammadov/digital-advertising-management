package com.digitaladvertisingmanagement.campaignmanagement.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "outbox_event")
public class OutboxEventJpaEntity {
  @Id private UUID id;

  @Column(name = "aggregate_id", nullable = false)
  private Long aggregateId;

  @Column(name = "event_type", nullable = false)
  private String eventType;

  @Column(name = "payload", nullable = false, columnDefinition = "TEXT")
  private String payload;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "published_at")
  private LocalDateTime publishedAt;

  protected OutboxEventJpaEntity() {}

  public OutboxEventJpaEntity(
      UUID id,
      Long aggregateId,
      String eventType,
      String payload,
      LocalDateTime createdAt,
      LocalDateTime publishedAt) {
    this.id = id;
    this.aggregateId = aggregateId;
    this.eventType = eventType;
    this.payload = payload;
    this.createdAt = createdAt;
    this.publishedAt = publishedAt;
  }

  public UUID getId() {
    return id;
  }

  public Long getAggregateId() {
    return aggregateId;
  }

  public String getEventType() {
    return eventType;
  }

  public String getPayload() {
    return payload;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public LocalDateTime getPublishedAt() {
    return publishedAt;
  }

  @PrePersist
  void onCreate() {
    this.createdAt = LocalDateTime.now();
  }
}
