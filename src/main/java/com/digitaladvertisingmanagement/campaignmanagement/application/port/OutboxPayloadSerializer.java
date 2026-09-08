package com.digitaladvertisingmanagement.campaignmanagement.application.port;

public interface OutboxPayloadSerializer {
  String serialize(Object object);
}
