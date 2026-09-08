package com.digitaladvertisingmanagement.campaignmanagement.infrastructure.messaging;

import com.digitaladvertisingmanagement.campaignmanagement.application.port.OutboxPayloadSerializer;
import org.springframework.stereotype.Component;
import tools.jackson.databind.json.JsonMapper;

@Component
public class OutboxPayloadJsonSerializer implements OutboxPayloadSerializer {

  private final JsonMapper jsonMapper;

  public OutboxPayloadJsonSerializer(JsonMapper objectMapper) {
    this.jsonMapper = objectMapper;
  }

  @Override
  public String serialize(Object payload) {
    return jsonMapper.writeValueAsString(payload);
  }
}
