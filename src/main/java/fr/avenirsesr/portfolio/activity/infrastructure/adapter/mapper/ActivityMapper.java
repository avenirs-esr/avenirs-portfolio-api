package fr.avenirsesr.portfolio.activity.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.activity.domain.model.Activity;
import fr.avenirsesr.portfolio.activity.infrastructure.adapter.model.ActivityEntity;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.EntityGrapher;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.mapper.Mapper;

public class ActivityMapper implements Mapper<ActivityEntity, Activity> {
  public static ActivityMapper INSTANCE = new ActivityMapper();

  @Override
  public ActivityEntity fromDomain(Activity domain) {
    return ActivityEntity.of(
        domain.getId(),
        domain.getTitle(),
        domain.getThematic(),
        domain.getSummary(),
        domain.getDescription(),
        domain.getExecutionPeriodInfo(),
        domain.getExecutionPeriodInfoSummary().orElse(null),
        domain.getCreatedAt(),
        domain.getUpdatedAt());
  }

  @Override
  public Activity toDomain(ActivityEntity entity) {
    return Activity.toDomain(
        entity.getId(),
        entity.getTitle(),
        entity.getThematic(),
        entity.getSummary(),
        entity.getDescription(),
        entity.getExecutionPeriodInfo(),
        entity.getExecutionPeriodInfoSummary(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }

  @Override
  public Activity toDomain(ActivityEntity entity, EntityGrapher<?> graph) {
    var attributs = graph.attributes();
    return Activity.toDomain(
        entity.getId(),
        entity.getTitle(),
        entity.getThematic(),
        entity.getSummary(),
        entity.getDescription(),
        entity.getExecutionPeriodInfo(),
        entity.getExecutionPeriodInfoSummary(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }
}
