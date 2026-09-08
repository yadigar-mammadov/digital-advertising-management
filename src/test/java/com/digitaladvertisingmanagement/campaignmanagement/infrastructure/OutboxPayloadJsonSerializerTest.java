package com.digitaladvertisingmanagement.campaignmanagement.infrastructure;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.digitaladvertisingmanagement.campaignmanagement.infrastructure.messaging.OutboxPayloadJsonSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.json.JsonMapper;

public class OutboxPayloadJsonSerializerTest {

  private OutboxPayloadJsonSerializer serializer;

  @BeforeEach
  void setUp() {
    JsonMapper jsonMapper = JsonMapper.builder().build();
    serializer = new OutboxPayloadJsonSerializer(jsonMapper);
  }

  @Test
  void shouldSerializePayloadToJson() {
    TestPayload payload = new TestPayload("campaign-123", "ACTIVE");

    String result = serializer.serialize(payload);

    assertEquals("{\"campaignId\":\"campaign-123\",\"status\":\"ACTIVE\"}", result);
  }

  private record TestPayload(String campaignId, String status) {}
}
