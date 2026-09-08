package com.digitaladvertisingmanagement.campaignmanagement.infrastructure;

import static org.mockito.Mockito.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.digitaladvertisingmanagement.campaignmanagement.domain.model.OutboxEvent;
import com.digitaladvertisingmanagement.campaignmanagement.domain.repository.OutboxEventRepository;
import com.digitaladvertisingmanagement.campaignmanagement.infrastructure.messaging.OutboxEventPublisher;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

@ExtendWith(MockitoExtension.class)
public class OutboxEventPublisherTest {

  @Mock private OutboxEventRepository outboxEventRepository;

  @Mock private KafkaTemplate<String, String> kafkaTemplate;

  @Mock private OutboxEvent event;

  @InjectMocks private OutboxEventPublisher publisher;

  private final String payload = "{\"name\":\"campaign\"}";

  private final String topic = "campaign-create";

  @Test
  void shouldPublishEventAndMarkItAsPublished() {
    Long aggregateId = 123L;

    when(event.getAggregateId()).thenReturn(aggregateId);
    when(event.getPayload()).thenReturn(payload);

    when(outboxEventRepository.findUnpublished(100)).thenReturn(List.of(event));

    when(kafkaTemplate.send(topic, aggregateId.toString(), payload))
        .thenReturn(CompletableFuture.completedFuture(null));

    publisher.publish();

    verify(kafkaTemplate).send(topic, aggregateId.toString(), payload);

    verify(event).markPublished();
    verify(outboxEventRepository).saveAll(List.of(event));
  }

  @Test
  void shouldNotMarkEventAsPublishedWhenKafkaPublishingFails() {
    Long aggregateId = 123L;

    when(event.getAggregateId()).thenReturn(aggregateId);
    when(event.getPayload()).thenReturn(payload);

    when(outboxEventRepository.findUnpublished(100)).thenReturn(List.of(event));

    CompletableFuture<SendResult<String, String>> failedFuture = new CompletableFuture<>();
    failedFuture.completeExceptionally(new RuntimeException("Kafka unavailable"));

    when(kafkaTemplate.send(topic, aggregateId.toString(), payload)).thenReturn(failedFuture);

    publisher.publish();

    verify(kafkaTemplate).send(topic, aggregateId.toString(), payload);

    verify(event, never()).markPublished();
    verify(outboxEventRepository, never()).saveAll(anyList());
  }

  @Test
  void shouldDoNothingWhenThereAreNoUnpublishedEvents() {
    when(outboxEventRepository.findUnpublished(100)).thenReturn(List.of());

    publisher.publish();

    verifyNoInteractions(kafkaTemplate);
    verify(outboxEventRepository, never()).saveAll(anyList());
  }
}
