package fr.avenirsesr.portfolio.activity.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.activity.infrastructure.adapter.model.ActivityEntity;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.EntityGrapher;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.mapper.Mapper;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.StaffMapper;

public class ActivityMapper implements Mapper<ActivityEntity, Activity> {
  public static ActivityMapper INSTANCE = new ActivityMapper();

  @Override
  public ActivityEntity fromDomain(Activity domain) {
    return ActivityEntity.of(
        domain.getId(),
        StaffMapper.INSTANCE.fromDomain(domain.getAuthor()),
        domain.getTitle(),
        domain.getThematic(),
        domain.getSummary(),
        domain.getDescription(),
        domain.getExecutionPeriodInfo(),
        domain.getExecutionPeriodInfoSummary().orElse(null),
        domain.getTraceAllowedAssociations(),
        domain.getFeedbackAllowedIterations(),
        domain.isEnableReflection(),
        domain.getCreatedAt(),
        domain.getUpdatedAt());
  }

  @Override
  public Activity toDomain(ActivityEntity entity) {
    return Activity.toDomain(
        entity.getId(),
        StaffMapper.INSTANCE.toDomain(entity.getAuthor()),
        entity.getTitle(),
        entity.getThematic(),
        entity.getSummary(),
        entity.getDescription(),
        entity.getExecutionPeriodInfo(),
        entity.getExecutionPeriodInfoSummary(),
        entity.isEnableReflection(),
        entity.getTraceAllowedAssociations(),
        entity.getFeedbackAllowedIterations(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }

  @Override
  public Activity toDomain(ActivityEntity entity, EntityGrapher<?> graph) {
    var attributes = graph.attributes();
    return Activity.toDomain(
        entity.getId(),
        attributes.contains("author") ? StaffMapper.INSTANCE.toDomain(entity.getAuthor()) : null,
        entity.getTitle(),
        entity.getThematic(),
        entity.getSummary(),
        entity.getDescription(),
        entity.getExecutionPeriodInfo(),
        entity.getExecutionPeriodInfoSummary(),
        entity.isEnableReflection(),
        entity.getTraceAllowedAssociations(),
        entity.getFeedbackAllowedIterations(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }
}
