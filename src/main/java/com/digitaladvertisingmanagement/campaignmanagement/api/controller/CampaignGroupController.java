package com.digitaladvertisingmanagement.campaignmanagement.api.controller;

import com.digitaladvertisingmanagement.auth.application.security.AuthenticatedUser;
import com.digitaladvertisingmanagement.campaignmanagement.api.dto.CampaignGroupListResponse;
import com.digitaladvertisingmanagement.campaignmanagement.api.dto.CampaignGroupResponse;
import com.digitaladvertisingmanagement.campaignmanagement.api.dto.CreateCampaignGroupRequest;
import com.digitaladvertisingmanagement.campaignmanagement.application.command.CreateCampaignGroupCommand;
import com.digitaladvertisingmanagement.campaignmanagement.application.result.CampaignGroupDetailsResult;
import com.digitaladvertisingmanagement.campaignmanagement.application.result.CampaignGroupSummaryResult;
import com.digitaladvertisingmanagement.campaignmanagement.application.service.CampaignGroupService;
import com.digitaladvertisingmanagement.shared.pagination.PageRequestData;
import com.digitaladvertisingmanagement.shared.pagination.PageResult;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/campaign-groups")
public class CampaignGroupController {

  private final CampaignGroupService campaignGroupService;

  public CampaignGroupController(CampaignGroupService campaignGroupService) {
    this.campaignGroupService = campaignGroupService;
  }

  @PostMapping
  public ResponseEntity<CampaignGroupResponse> create(
      @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
      @Valid @RequestBody CreateCampaignGroupRequest request) {
    CampaignGroupDetailsResult result =
        campaignGroupService.create(
            new CreateCampaignGroupCommand(
                authenticatedUser.id(),
                request.name(),
                request.platforms(),
                request.configuration().objective(),
                request.configuration().budgetType(),
                request.configuration().budgetAmount(),
                request.configuration().startDate(),
                request.configuration().endDate()));
    return ResponseEntity.ok(CampaignGroupResponse.from(result));
  }

  @GetMapping
  public ResponseEntity<PageResult<CampaignGroupListResponse>> list(
      @AuthenticationPrincipal AuthenticatedUser authenticatedUser,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "10") int size) {
    PageRequestData pageRequest = new PageRequestData(page, size);

    PageResult<CampaignGroupSummaryResult> result =
        campaignGroupService.list(authenticatedUser.id(), pageRequest);

    PageResult<CampaignGroupListResponse> response = result.map(CampaignGroupListResponse::from);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/{id}")
  public ResponseEntity<CampaignGroupResponse> get(
      @AuthenticationPrincipal AuthenticatedUser authenticatedUser, @PathVariable Long id) {
    CampaignGroupDetailsResult result = campaignGroupService.get(authenticatedUser.id(), id);

    return ResponseEntity.ok(CampaignGroupResponse.from(result));
  }
}
