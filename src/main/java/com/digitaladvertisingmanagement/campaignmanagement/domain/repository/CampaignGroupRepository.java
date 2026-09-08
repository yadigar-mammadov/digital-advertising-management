package com.digitaladvertisingmanagement.campaignmanagement.domain.repository;

import com.digitaladvertisingmanagement.campaignmanagement.domain.model.CampaignGroup;
import com.digitaladvertisingmanagement.shared.pagination.PageRequestData;
import com.digitaladvertisingmanagement.shared.pagination.PageResult;
import java.util.Optional;

public interface CampaignGroupRepository {
  CampaignGroup save(CampaignGroup group);

  PageResult<CampaignGroup> findByOwnerId(Long ownerId, PageRequestData pageRequest);

  Optional<CampaignGroup> findByIdAndOwnerId(Long id, Long ownerId);
}
