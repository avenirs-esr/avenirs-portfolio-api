package fr.avenirsesr.portfolio.activity.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.activity.domain.model.ActivityDraft;
import fr.avenirsesr.portfolio.activity.infrastructure.adapter.model.ActivityDraftEntity;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.EntityGrapher;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.mapper.Mapper;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.StaffMapper;

public class ActivityDraftMapper implements Mapper<ActivityDraftEntity, ActivityDraft> {
  public static ActivityDraftMapper INSTANCE = new ActivityDraftMapper();

  @Override
  public ActivityDraftEntity fromDomain(ActivityDraft activityDraft) {
    return ActivityDraftEntity.of(
        activityDraft.getId(),
        activityDraft.getTitle(),
        StaffMapper.INSTANCE.fromDomain(activityDraft.getAuthor()),
        activityDraft.getThematic(),
        activityDraft.getSummary().orElse(null),
        activityDraft.getDescription().orElse(null),
        activityDraft.getExecutionPeriodInfo().orElse(null),
        activityDraft.getExecutionPeriodInfoSummary().orElse(null),
        activityDraft.getTraceAllowedAssociations().orElse(null),
        activityDraft.getFeedbackAllowedIterations().orElse(null),
        activityDraft.isEnableReflection(),
        activityDraft.getCreatedAt(),
        activityDraft.getUpdatedAt());
  }

  @Override
  public ActivityDraft toDomain(ActivityDraftEntity entity) {
    return ActivityDraft.toDomain(
        entity.getId(),
        entity.getCreatedAt(),
        entity.getUpdatedAt(),
        entity.getTitle(),
        StaffMapper.INSTANCE.toDomain(entity.getAuthor()),
        entity.getThematic(),
        entity.getSummary(),
        entity.getDescription(),
        entity.getExecutionPeriodInfo(),
        entity.getExecutionPeriodInfoSummary(),
        entity.getTraceAllowedAssociations(),
        entity.getFeedbackAllowedIterations(),
        entity.isEnableReflection());
  }

  @Override
  public ActivityDraft toDomain(ActivityDraftEntity entity, EntityGrapher<?> graph) {
    var attributes = graph.attributes();
    return ActivityDraft.toDomain(
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
        entity.getExecutionPeriodInfo(),
        entity.getExecutionPeriodInfoSummary(),
        entity.getTraceAllowedAssociations(),
        entity.getFeedbackAllowedIterations(),
        entity.isEnableReflection());
  }
}
