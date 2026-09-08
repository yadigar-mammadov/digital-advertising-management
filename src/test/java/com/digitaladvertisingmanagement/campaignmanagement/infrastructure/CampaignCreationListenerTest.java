package com.digitaladvertisingmanagement.campaignmanagement.infrastructure;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.digitaladvertisingmanagement.campaignmanagement.application.messaging.CreateCampaignMessage;
import com.digitaladvertisingmanagement.campaignmanagement.application.service.CampaignCreationService;
import com.digitaladvertisingmanagement.campaignmanagement.infrastructure.messaging.CampaignCreationListener;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tools.jackson.databind.json.JsonMapper;

@ExtendWith(MockitoExtension.class)
public class CampaignCreationListenerTest {

  @Mock private JsonMapper jsonMapper;

  @Mock private CampaignCreationService campaignCreationService;

  @InjectMocks private CampaignCreationListener listener;

  @Test
  void shouldDeserializePayloadAndCreateCampaign() {
    // Arrange
    String payload =
        """
                {
                    "campaignId": "123"
                }
                """;

    CreateCampaignMessage message = mock(CreateCampaignMessage.class);

    when(jsonMapper.readValue(payload, CreateCampaignMessage.class)).thenReturn(message);

    // Act
    listener.consume(payload);

    // Assert
    verify(jsonMapper).readValue(payload, CreateCampaignMessage.class);

    verify(campaignCreationService).create(message);
  }
}
