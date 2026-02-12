package fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.activity.infrastructure.adapter.mapper.ActivityMapper;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.EntityGrapher;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.mapper.Mapper;
import fr.avenirsesr.portfolio.student.progress.imported.domain.model.ActivityProgress;
import fr.avenirsesr.portfolio.student.progress.imported.infrastructure.adapter.model.ActivityProgressEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.StudentMapper;

public class ActivityProgressMapper
    implements Mapper<ActivityProgressEntity, ActivityProgress> {
  public static final ActivityProgressMapper INSTANCE = new ActivityProgressMapper();

  @Override
  public ActivityProgressEntity fromDomain(ActivityProgress activityProgress) {
    return ActivityProgressEntity.of(
        activityProgress.getId(),
        StudentMapper.INSTANCE.fromDomain(activityProgress.getStudent()),
        ActivityMapper.INSTANCE.fromDomain(activityProgress.getActivity()),
        activityProgress.getStatus(),
        activityProgress.getReflection(),
        activityProgress.getStartDate(),
        activityProgress.getEndDate());
  }

  @Override
  public ActivityProgress toDomain(ActivityProgressEntity entity) {
    return ActivityProgress.toDomain(
        entity.getId(),
        StudentMapper.INSTANCE.toDomain(entity.getStudent()),
        ActivityMapper.INSTANCE.toDomain(entity.getActivity()),
        entity.getStatus(),
        entity.getReflection(),
        entity.getStartDate(),
        entity.getEndDate(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }

  @Override
  public ActivityProgress toDomain(ActivityProgressEntity entity, EntityGrapher<?> graph) {
    var attributs = graph.attributes();
    return ActivityProgress.toDomain(
        entity.getId(),
        attributs.contains("student")
            ? StudentMapper.INSTANCE.toDomain(entity.getStudent(), graph.from("student"))
            : null,
        attributs.contains("activity")
            ? ActivityMapper.INSTANCE.toDomain(entity.getActivity(), graph.from("activity"))
            : null,
            entity.getStatus(),
            entity.getReflection(),
        entity.getStartDate(),
        entity.getEndDate(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }
}
