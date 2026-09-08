package com.digitaladvertisingmanagement.campaignmanagement.domain.repository;

import com.digitaladvertisingmanagement.campaignmanagement.domain.model.Campaign;
import java.util.List;
import java.util.Optional;

public interface CampaignRepository {
  Campaign save(Campaign campaign);

  List<Campaign> saveALl(List<Campaign> campaigns);

  Optional<Campaign> findById(Long id);

  List<Campaign> findByCampaignGroupId(Long campaignGroupId);

  boolean tryMarkProcessing(Long campaignId);

  void markCreated(Long campaignId, String externalCampaignId);

  void markFailed(Long campaignId, String failureReason, String externalCampaignId);
}
