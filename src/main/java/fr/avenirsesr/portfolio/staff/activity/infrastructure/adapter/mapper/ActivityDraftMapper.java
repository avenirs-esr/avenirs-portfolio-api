package fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.EntityGrapher;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.mapper.Mapper;
import fr.avenirsesr.portfolio.file.infrastructure.adapter.mapper.FileMapper;
import fr.avenirsesr.portfolio.staff.activity.domain.model.ActivityDraft;
import fr.avenirsesr.portfolio.staff.activity.infrastructure.adapter.model.ActivityDraftEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.StaffMapper;

public class ActivityDraftMapper implements Mapper<ActivityDraftEntity, ActivityDraft> {
  public static ActivityDraftMapper INSTANCE = new ActivityDraftMapper();

  @Override
  public ActivityDraftEntity fromDomain(ActivityDraft activityDraft) {
    var entity =
        ActivityDraftEntity.of(
            activityDraft.getId(),
            activityDraft.getTitle(),
            StaffMapper.INSTANCE.fromDomain(activityDraft.getAuthor()),
            activityDraft.getThematic().orElse(null),
            activityDraft.getSummary().orElse(null),
            activityDraft.getDescription().orElse(null),
            activityDraft.getRecommendedCompletionContexts().orElse(null),
            activityDraft.getStartDate().orElse(null),
            activityDraft.getEndDate().orElse(null),
            activityDraft.getTraceAllowedAssociations(),
            activityDraft.getFeedbackAllowedIterations(),
            activityDraft.isEnableReflection(),
            activityDraft.getBanner().map(FileMapper.INSTANCE::fromDomain).orElse(null),
            activityDraft.getLinks(),
            activityDraft.getFiles().stream().map(FileMapper.INSTANCE::fromDomain).toList(),
            activityDraft.getCreatedAt(),
            activityDraft.getUpdatedAt());
    entity.setTargetInstitutionIds(activityDraft.getTargetInstitutionIds());
    entity.setTargetGroupIds(activityDraft.getTargetGroupIds());
    return entity;
  }

  @Override
  public ActivityDraft toDomain(ActivityDraftEntity entity) {
    var domain =
        ActivityDraft.toDomain(
            entity.getId(),
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            entity.getTitle(),
            StaffMapper.INSTANCE.toDomain(entity.getAuthor()),
            entity.getThematic(),
            entity.getSummary(),
            entity.getDescription(),
            entity.getRecommendedCompletionContexts(),
            entity.getStartDate(),
            entity.getEndDate(),
            entity.getTraceAllowedAssociations(),
            entity.getFeedbackAllowedIterations(),
            entity.isEnableReflection(),
            entity.getBanner() == null ? null : FileMapper.INSTANCE.toDomain(entity.getBanner()),
            entity.getLinks(),
            entity.getFiles().stream().map(FileMapper.INSTANCE::toDomain).toList());
    domain.setTargetInstitutionIds(entity.getTargetInstitutionIds());
    domain.setTargetGroupIds(entity.getTargetGroupIds());
    return domain;
  }

  @Override
  public ActivityDraft toDomain(ActivityDraftEntity entity, EntityGrapher<?> graph) {
    var attributes = graph.attributes();
    var domain =
        ActivityDraft.toDomain(
            entity.getId(),
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            entity.getTitle(),
            attributes.contains("author")
                ? StaffMapper.INSTANCE.toDomain(entity.getAuthor(), graph)
                : null,
            entity.getThematic(),
            entity.getSummary(),
            entity.getDescription(),
            entity.getRecommendedCompletionContexts(),
            entity.getStartDate(),
            entity.getEndDate(),
            entity.getTraceAllowedAssociations(),
            entity.getFeedbackAllowedIterations(),
            entity.isEnableReflection(),
            attributes.contains("banner") && entity.getBanner() != null
                ? FileMapper.INSTANCE.toDomain(entity.getBanner(), graph)
                : null,
            entity.getLinks(),
            entity.getFiles().stream().map(FileMapper.INSTANCE::toDomain).toList());
    domain.setTargetInstitutionIds(entity.getTargetInstitutionIds());
    domain.setTargetGroupIds(entity.getTargetGroupIds());
    return domain;
  }
}
