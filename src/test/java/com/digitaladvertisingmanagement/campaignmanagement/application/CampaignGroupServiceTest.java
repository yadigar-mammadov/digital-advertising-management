package com.digitaladvertisingmanagement.campaignmanagement.application;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.digitaladvertisingmanagement.campaignmanagement.application.command.CreateCampaignGroupCommand;
import com.digitaladvertisingmanagement.campaignmanagement.application.exception.CampaignGroupNotFoundException;
import com.digitaladvertisingmanagement.campaignmanagement.application.port.OutboxPayloadSerializer;
import com.digitaladvertisingmanagement.campaignmanagement.application.result.CampaignGroupDetailsResult;
import com.digitaladvertisingmanagement.campaignmanagement.application.result.CampaignGroupSummaryResult;
import com.digitaladvertisingmanagement.campaignmanagement.application.service.CampaignGroupService;
import com.digitaladvertisingmanagement.campaignmanagement.domain.exception.DomainValidationException;
import com.digitaladvertisingmanagement.campaignmanagement.domain.model.Campaign;
import com.digitaladvertisingmanagement.campaignmanagement.domain.model.CampaignConfiguration;
import com.digitaladvertisingmanagement.campaignmanagement.domain.model.CampaignGroup;
import com.digitaladvertisingmanagement.campaignmanagement.domain.model.enums.BudgetType;
import com.digitaladvertisingmanagement.campaignmanagement.domain.model.enums.CampaignGroupStatus;
import com.digitaladvertisingmanagement.campaignmanagement.domain.repository.CampaignGroupRepository;
import com.digitaladvertisingmanagement.campaignmanagement.domain.repository.CampaignRepository;
import com.digitaladvertisingmanagement.campaignmanagement.domain.repository.OutboxEventRepository;
import com.digitaladvertisingmanagement.shared.pagination.PageRequestData;
import com.digitaladvertisingmanagement.shared.pagination.PageResult;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class CampaignGroupServiceTest {
  @Mock private CampaignGroupRepository campaignGroupRepository;

  @Mock private CampaignRepository campaignRepository;

  @Mock private OutboxEventRepository outboxEventRepository;

  @Mock private OutboxPayloadSerializer outboxPayloadSerializer;

  @InjectMocks private CampaignGroupService service;

  @Test
  void createShouldSaveCampaignGroup() {
    CreateCampaignGroupCommand command = mock(CreateCampaignGroupCommand.class);

    when(command.ownerId()).thenReturn(10L);
    when(command.name()).thenReturn("Test Campaign Group");
    when(command.objective()).thenReturn("TRAFFIC");
    when(command.budgetType()).thenReturn(BudgetType.DAILY);
    when(command.budgetAmount()).thenReturn(BigDecimal.valueOf(1000));
    when(command.startDate()).thenReturn(LocalDateTime.now());
    when(command.endDate()).thenReturn(LocalDateTime.now().plusDays(30));

    when(command.platforms()).thenReturn(Set.of());

    CampaignGroup savedGroup = mock(CampaignGroup.class);
    CampaignConfiguration configuration = mock(CampaignConfiguration.class);

    when(savedGroup.getId()).thenReturn(100L);
    when(savedGroup.getOwnerId()).thenReturn(10L);
    when(savedGroup.getName()).thenReturn("Test Campaign Group");
    when(savedGroup.getConfiguration()).thenReturn(configuration);

    when(campaignGroupRepository.save(any(CampaignGroup.class))).thenReturn(savedGroup);

    when(campaignRepository.saveALl(anyList())).thenReturn(List.of());

    CampaignGroupDetailsResult result = service.create(command);

    assertNotNull(result);
    assertEquals(100L, result.id());
    assertEquals(10L, result.ownerId());
    assertEquals("Test Campaign Group", result.name());

    verify(campaignGroupRepository).save(any(CampaignGroup.class));
    verify(campaignRepository).saveALl(anyList());
    verify(outboxEventRepository).saveAll(List.of());
  }

  static Stream<Arguments> invalidConfigurationArguments() {
    return Stream.of(
        Arguments.of(
            null, "TRAFFIC", BudgetType.DAILY, BigDecimal.valueOf(100), "Owner id is required."),
        Arguments.of(1L, null, BudgetType.DAILY, BigDecimal.valueOf(100), "Objective is required."),
        Arguments.of(1L, "", BudgetType.DAILY, BigDecimal.valueOf(100), "Objective is required."),
        Arguments.of(1L, "TRAFFIC", null, BigDecimal.valueOf(100), "Budget type is required."),
        Arguments.of(1L, "TRAFFIC", BudgetType.DAILY, null, "Budget amount must be positive."),
        Arguments.of(
            1L, "TRAFFIC", BudgetType.DAILY, BigDecimal.ZERO, "Budget amount must be positive."),
        Arguments.of(
            1L,
            "TRAFFIC",
            BudgetType.DAILY,
            BigDecimal.valueOf(-1),
            "Budget amount must be positive."));
  }

  @ParameterizedTest
  @MethodSource("invalidConfigurationArguments")
  void createShouldThrowExceptionForInvalidArguments(
      Long ownerId,
      String objective,
      BudgetType budgetType,
      BigDecimal budgetAmount,
      String expectedMessage) {
    CreateCampaignGroupCommand command = mock(CreateCampaignGroupCommand.class);

    lenient().when(command.ownerId()).thenReturn(ownerId);
    lenient().when(command.name()).thenReturn("Test Campaign");
    lenient().when(command.objective()).thenReturn(objective);
    lenient().when(command.budgetType()).thenReturn(budgetType);
    lenient().when(command.budgetAmount()).thenReturn(budgetAmount);

    DomainValidationException exception =
        assertThrows(DomainValidationException.class, () -> service.create(command));

    assertEquals(expectedMessage, exception.getMessage());
  }

  @Test
  void getShouldReturnCampaignGroupDetails() {
    Long ownerId = 10L;
    Long campaignGroupId = 100L;

    CampaignGroup campaignGroup = mock(CampaignGroup.class);
    CampaignConfiguration configuration = mock(CampaignConfiguration.class);
    Campaign campaign = mock(Campaign.class);

    when(campaignGroup.getId()).thenReturn(campaignGroupId);
    when(campaignGroup.getOwnerId()).thenReturn(ownerId);
    when(campaignGroup.getName()).thenReturn("Test Group");
    when(campaignGroup.getConfiguration()).thenReturn(configuration);

    when(configuration.getId()).thenReturn(1L);
    when(configuration.getBudgetAmount()).thenReturn(BigDecimal.valueOf(1000));

    when(campaign.getId()).thenReturn(500L);
    when(campaign.getName()).thenReturn("Test Group");

    when(campaignGroupRepository.findByIdAndOwnerId(campaignGroupId, ownerId))
        .thenReturn(Optional.of(campaignGroup));

    when(campaignRepository.findByCampaignGroupId(campaignGroupId)).thenReturn(List.of(campaign));

    CampaignGroupDetailsResult result = service.get(ownerId, campaignGroupId);

    assertNotNull(result);
    assertEquals(campaignGroupId, result.id());
    assertEquals(ownerId, result.ownerId());
    assertEquals("Test Group", result.name());

    assertNotNull(result.configuration());

    assertEquals(1, result.campaigns().size());
    assertEquals(500L, result.campaigns().getFirst().id());

    verify(campaignGroupRepository).findByIdAndOwnerId(campaignGroupId, ownerId);

    verify(campaignRepository).findByCampaignGroupId(campaignGroupId);
  }

  @Test
  void getShouldThrowExceptionWhenCampaignGroupDoesNotExist() {
    Long ownerId = 10L;
    Long campaignGroupId = 999L;

    when(campaignGroupRepository.findByIdAndOwnerId(campaignGroupId, ownerId))
        .thenReturn(Optional.empty());

    CampaignGroupNotFoundException exception =
        assertThrows(
            CampaignGroupNotFoundException.class, () -> service.get(ownerId, campaignGroupId));

    assertEquals(
        "Campaign group not found. id=" + campaignGroupId + ", ownerId=" + ownerId,
        exception.getMessage());

    verify(campaignGroupRepository).findByIdAndOwnerId(campaignGroupId, ownerId);

    verifyNoInteractions(campaignRepository);
  }

  @Test
  @SuppressWarnings("unchecked")
  void listShouldReturnCampaignGroupsForOwner() {
    Long ownerId = 10L;

    PageRequestData pageRequest = mock(PageRequestData.class);

    CampaignGroup campaignGroup = mock(CampaignGroup.class);

    when(campaignGroup.getId()).thenReturn(100L);
    when(campaignGroup.getOwnerId()).thenReturn(ownerId);
    when(campaignGroup.getName()).thenReturn("Campaign Group");
    when(campaignGroup.getStatus()).thenReturn(CampaignGroupStatus.ACTIVE);

    PageResult<CampaignGroup> campaignGroupPage = mock(PageResult.class);

    PageResult<CampaignGroupSummaryResult> mappedPage = mock(PageResult.class);

    when(campaignGroupRepository.findByOwnerId(ownerId, pageRequest)).thenReturn(campaignGroupPage);

    when(campaignGroupPage.<CampaignGroupSummaryResult>map(any())).thenReturn(mappedPage);

    PageResult<CampaignGroupSummaryResult> result = service.list(ownerId, pageRequest);

    ArgumentCaptor<Function<CampaignGroup, CampaignGroupSummaryResult>> mapperCaptor =
        ArgumentCaptor.forClass(Function.class);

    verify(campaignGroupRepository).findByOwnerId(ownerId, pageRequest);

    verify(campaignGroupPage).map(mapperCaptor.capture());

    CampaignGroupSummaryResult mappedResult = mapperCaptor.getValue().apply(campaignGroup);

    assertEquals(100L, mappedResult.id());
    assertEquals(ownerId, mappedResult.ownerId());
    assertEquals("Campaign Group", mappedResult.name());
    assertEquals(CampaignGroupStatus.ACTIVE, mappedResult.status());

    assertSame(mappedPage, result);
  }
}
