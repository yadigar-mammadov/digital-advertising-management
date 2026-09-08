package com.digitaladvertisingmanagement.campaignmanagement.infrastructure.messaging;

import com.digitaladvertisingmanagement.campaignmanagement.domain.model.OutboxEvent;
import com.digitaladvertisingmanagement.campaignmanagement.domain.repository.OutboxEventRepository;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class OutboxEventPublisher {

  private static final Logger log = LoggerFactory.getLogger(OutboxEventPublisher.class);

  private static final String TOPIC = "campaign-create";
  private static final int BATCH_SIZE = 100;

  private final OutboxEventRepository outboxEventRepository;
  private final KafkaTemplate<String, String> kafkaTemplate;

  public OutboxEventPublisher(
      OutboxEventRepository outboxEventRepository, KafkaTemplate<String, String> kafkaTemplate) {
    this.outboxEventRepository = outboxEventRepository;
    this.kafkaTemplate = kafkaTemplate;
  }

  @Scheduled(fixedDelay = 1000)
  public void publish() {
    List<OutboxEvent> unpublishedEvents = outboxEventRepository.findUnpublished(BATCH_SIZE);
    List<OutboxEvent> publishedEvents = new ArrayList<>();

    for (OutboxEvent event : unpublishedEvents) {
      try {
        kafkaTemplate.send(TOPIC, event.getAggregateId().toString(), event.getPayload()).join();

        event.markPublished();
        publishedEvents.add(event);

        log.info(
            "Published outbox event. eventId={}, aggregateId={}, eventType={}",
            event.getId(),
            event.getAggregateId(),
            event.getEventType());

      } catch (Exception e) {
        log.error(
            "Failed to publish outbox event. eventId={}, aggregateId={}, eventType={}",
            event.getId(),
            event.getAggregateId(),
            event.getEventType(),
            e);
      }
    }

    if (!publishedEvents.isEmpty()) {
      outboxEventRepository.saveAll(publishedEvents);
    }
  }
}
