package com.digitaladvertisingmanagement.campaignmanagement.api;

import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.authentication;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.digitaladvertisingmanagement.auth.application.security.AuthenticatedUser;
import java.util.List;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class CampaignGroupControllerIntegrationTest {
  @Autowired private MockMvc mockMvc;

  @Autowired private JsonMapper jsonMapper;

  private static final Long OWNER_ID = 10L;

  private final String requestBody =
      """
                {
                  "name": "Summer Campaign",
                  "platforms": [
                    "GOOGLE",
                    "META"
                  ],
                  "configuration": {
                    "objective": "TRAFFIC",
                    "budgetType": "DAILY",
                    "budgetAmount": 1000,
                    "startDate": "2026-09-10T00:00:00",
                    "endDate": "2026-10-10T23:59:59"
                  }
                }
                """;

  @Test
  void shouldCreateCampaignGroup() throws Exception {
    mockMvc
        .perform(
            post("/api/campaign-groups")
                .with(authentication(testAuthentication(OWNER_ID)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").exists())
        .andExpect(jsonPath("$.ownerId").value(OWNER_ID))
        .andExpect(jsonPath("$.name").value("Summer Campaign"))
        .andExpect(jsonPath("$.campaigns").isArray())
        .andExpect(jsonPath("$.campaigns.length()").value(2));
  }

  @Test
  void shouldCreateAndGetCampaignGroup() throws Exception {

    Long campaignGroupId = createCampaignGroup(OWNER_ID);

    mockMvc
        .perform(
            get("/api/campaign-groups/{id}", campaignGroupId)
                .with(authentication(testAuthentication(OWNER_ID))))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value(campaignGroupId))
        .andExpect(jsonPath("$.ownerId").value(OWNER_ID))
        .andExpect(jsonPath("$.name").value("Summer Campaign"))
        .andExpect(jsonPath("$.campaigns.length()").value(2));
  }

  @Test
  void shouldNotAllowOwnerToGetAnotherOwnersCampaignGroup() throws Exception {

    Long owner1 = 10L;
    Long owner2 = 20L;

    Long campaignGroupId = createCampaignGroup(owner1);

    mockMvc
        .perform(
            get("/api/campaign-groups/{id}", campaignGroupId)
                .with(authentication(testAuthentication(owner2))))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldReturnNotFoundWhenCampaignGroupDoesNotExist() throws Exception {

    mockMvc
        .perform(
            get("/api/campaign-groups/{id}", 999999L)
                .with(authentication(testAuthentication(OWNER_ID))))
        .andExpect(status().isNotFound());
  }

  private static Stream<Arguments> invalidCreateCampaignRequests() {
    return Stream.of(
        Arguments.of(
            "invalid platform",
            """
                        {
                          "name": "Summer Campaign",
                          "platforms": ["GOOGLE_ADS"],
                          "configuration": {
                            "objective": "TRAFFIC",
                            "budgetType": "DAILY",
                            "budgetAmount": 1000,
                            "startDate": "2026-09-10T00:00:00",
                            "endDate": "2026-10-10T23:59:59"
                          }
                        }
                        """),
        Arguments.of(
            "invalid start date",
            """
                        {
                          "name": "Summer Campaign",
                          "platforms": ["GOOGLE"],
                          "configuration": {
                            "objective": "TRAFFIC",
                            "budgetType": "DAILY",
                            "budgetAmount": 1000,
                            "startDate": "invalid-date",
                            "endDate": "2026-10-10T23:59:59"
                          }
                        }
                        """),
        Arguments.of(
            "blank name",
            """
                        {
                          "name": "",
                          "platforms": ["GOOGLE"],
                          "configuration": {
                            "objective": "TRAFFIC",
                            "budgetType": "DAILY",
                            "budgetAmount": 1000,
                            "startDate": "2026-09-10T00:00:00",
                            "endDate": "2026-10-10T23:59:59"
                          }
                        }
                        """),
        Arguments.of(
            "empty platforms",
            """
                        {
                          "name": "Summer Campaign",
                          "platforms": [],
                          "configuration": {
                            "objective": "TRAFFIC",
                            "budgetType": "DAILY",
                            "budgetAmount": 1000,
                            "startDate": "2026-09-10T00:00:00",
                            "endDate": "2026-10-10T23:59:59"
                          }
                        }
                        """),
        Arguments.of(
            "invalid budget type",
            """
                        {
                          "name": "Summer Campaign",
                          "platforms": ["GOOGLE"],
                          "configuration": {
                            "objective": "TRAFFIC",
                            "budgetType": "INVALID",
                            "budgetAmount": 1000,
                            "startDate": "2026-09-10T00:00:00",
                            "endDate": "2026-10-10T23:59:59"
                          }
                        }
                        """),
        Arguments.of(
            "malformed JSON",
            """
                        {
                          "name": "Summer Campaign",
                          "platforms":
                        }
                        """));
  }

  @ParameterizedTest(name = "{0}")
  @MethodSource("invalidCreateCampaignRequests")
  void shouldReturnBadRequestForInvalidCreateCampaignRequest(String scenario, String requestBody)
      throws Exception {

    mockMvc
        .perform(
            post("/api/campaign-groups")
                .with(authentication(testAuthentication(OWNER_ID)))
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestBody))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldListCampaignGroups() throws Exception {

    createCampaignGroup(OWNER_ID);

    mockMvc
        .perform(
            get("/api/campaign-groups")
                .with(authentication(testAuthentication(OWNER_ID)))
                .param("page", "0")
                .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items").isArray())
        .andExpect(jsonPath("$.items.length()").value(1))
        .andExpect(jsonPath("$.items[0].name").value("Summer Campaign"));
  }

  @Test
  void shouldReturnOnlyCampaignGroupsBelongingToCurrentOwner() throws Exception {

    Long owner1 = 10L;
    Long owner2 = 20L;

    createCampaignGroup(owner1);
    createCampaignGroup(owner1);

    createCampaignGroup(owner2);

    mockMvc
        .perform(
            get("/api/campaign-groups")
                .with(authentication(testAuthentication(owner1)))
                .param("page", "0")
                .param("size", "10"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.items.length()").value(2))
        .andExpect(jsonPath("$.items[*].ownerId", everyItem(is(owner1.intValue()))));
  }

  @Test
  void shouldRejectUnauthenticatedRequest() throws Exception {

    mockMvc.perform(get("/api/campaign-groups")).andExpect(status().isUnauthorized());
  }

  private Long createCampaignGroup(Long ownerId) throws Exception {
    String response =
        mockMvc
            .perform(
                post("/api/campaign-groups")
                    .with(authentication(testAuthentication(ownerId)))
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(requestBody))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();

    JsonNode json = jsonMapper.readTree(response);

    return json.get("id").asLong();
  }

  private Authentication testAuthentication(Long ownerId) {

    AuthenticatedUser authenticatedUser = mock(AuthenticatedUser.class);
    when(authenticatedUser.id()).thenReturn(ownerId);

    return new UsernamePasswordAuthenticationToken(authenticatedUser, null, List.of());
  }
}
