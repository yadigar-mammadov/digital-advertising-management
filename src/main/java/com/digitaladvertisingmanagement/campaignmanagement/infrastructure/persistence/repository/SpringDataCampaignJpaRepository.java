package com.digitaladvertisingmanagement.campaignmanagement.infrastructure.persistence.repository;

import com.digitaladvertisingmanagement.campaignmanagement.domain.model.enums.CampaignStatus;
import com.digitaladvertisingmanagement.campaignmanagement.infrastructure.persistence.entity.CampaignJpaEntity;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

interface SpringDataCampaignJpaRepository extends JpaRepository<CampaignJpaEntity, Long> {
  List<CampaignJpaEntity> findByCampaignGroupId(Long campaignGroupId);

  @Modifying
  @Query(
      """
        update CampaignJpaEntity c
        set c.status = :processing
        where c.id = :campaignId
          and c.status in (:pending, :failed)
    """)
  int tryMarkProcessing(
      @Param("campaignId") Long campaignId,
      @Param("processing") CampaignStatus processing,
      @Param("pending") CampaignStatus pending,
      @Param("failed") CampaignStatus failed);

  @Modifying
  @Query(
      """
        update CampaignJpaEntity c
        set c.status = :created,
            c.externalCampaignId = :externalCampaignId,
            c.failureReason = null
        where c.id = :campaignId
          and c.status = :processing
    """)
  int markCreated(
      @Param("campaignId") Long campaignId,
      @Param("externalCampaignId") String externalCampaignId,
      @Param("created") CampaignStatus created,
      @Param("processing") CampaignStatus processing);

  @Modifying
  @Query(
      """
        update CampaignJpaEntity c
        set c.status = :failed,
            c.failureReason = :failureReason,
            c.externalCampaignId = :externalCampaignId
        where c.id = :campaignId
          and c.status = :processing
    """)
  int markFailed(
      @Param("campaignId") Long campaignId,
      @Param("failureReason") String failureReason,
      @Param("externalCampaignId") String externalCampaignId,
      @Param("failed") CampaignStatus failed,
      @Param("processing") CampaignStatus processing);
}
