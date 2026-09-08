package com.digitaladvertisingmanagement.campaignmanagement.domain.repository;

import com.digitaladvertisingmanagement.campaignmanagement.domain.model.OutboxEvent;
import java.util.List;

public interface OutboxEventRepository {
  OutboxEvent save(OutboxEvent outboxEvent);

  List<OutboxEvent> findUnpublished(int limit);

  void saveAll(List<OutboxEvent> events);
}
