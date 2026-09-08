package com.digitaladvertisingmanagement.campaignmanagement.domain.model;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

public class OutboxEvent {
  private final UUID id;
  private final Long aggregateId;
  private final String eventType;
  private final String payload;
  private final LocalDateTime createdAt;
  private LocalDateTime publishedAt;

  private OutboxEvent(
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

  public static OutboxEvent create(Long aggregateId, String eventType, String payload) {
    if (eventType == null || eventType.isBlank()) {
      throw new IllegalArgumentException("eventType must not be blank");
    }

    if (payload == null) {
      throw new IllegalArgumentException("payload must not be null");
    }

    return new OutboxEvent(
        UUID.randomUUID(), aggregateId, eventType, payload, LocalDateTime.now(), null);
  }

  public static OutboxEvent restore(
      UUID id,
      Long aggregateId,
      String eventType,
      String payload,
      LocalDateTime createdAt,
      LocalDateTime publishedAt) {
    Objects.requireNonNull(id, "id must not be null");
    Objects.requireNonNull(createdAt, "createdAt must not be null");

    if (eventType == null || eventType.isBlank()) {
      throw new IllegalArgumentException("eventType must not be blank");
    }

    if (payload == null) {
      throw new IllegalArgumentException("payload must not be null");
    }

    return new OutboxEvent(id, aggregateId, eventType, payload, createdAt, publishedAt);
  }

  public void markPublished() {
    this.publishedAt = LocalDateTime.now();
  }

  public boolean isPublished() {
    return publishedAt != null;
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
}
