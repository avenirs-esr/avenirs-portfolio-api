package fr.avenirsesr.portfolio.student.progress.declared.activity.infrastructure.adapter.mapper;

import fr.avenirsesr.portfolio.activity.infrastructure.adapter.mapper.ActivityMapper;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.EntityGrapher;
import fr.avenirsesr.portfolio.common.data.infrastructure.adapter.mapper.Mapper;
import fr.avenirsesr.portfolio.student.progress.declared.activity.domain.model.DeclaredActivity;
import fr.avenirsesr.portfolio.student.progress.declared.activity.infrastructure.adapter.model.DeclaredActivityEntity;
import fr.avenirsesr.portfolio.user.infrastructure.adapter.mapper.StudentMapper;

public class DeclaredActivityMapper implements Mapper<DeclaredActivityEntity, DeclaredActivity> {
  public static final DeclaredActivityMapper INSTANCE = new DeclaredActivityMapper();

  @Override
  public DeclaredActivityEntity fromDomain(DeclaredActivity declaredActivity) {
    return DeclaredActivityEntity.of(
        declaredActivity.getId(),
        StudentMapper.INSTANCE.fromDomain(declaredActivity.getStudent()),
        ActivityMapper.INSTANCE.fromDomain(declaredActivity.getActivity()),
        declaredActivity.getStartedAt().orElse(null),
        declaredActivity.getReflection(),
        declaredActivity.getStartDate(),
        declaredActivity.getEndDate(),
        declaredActivity.getFinishedAt().orElse(null),
        declaredActivity.getCreatedAt(),
        declaredActivity.getUpdatedAt());
  }

  @Override
  public DeclaredActivity toDomain(DeclaredActivityEntity entity) {
    return DeclaredActivity.toDomain(
        entity.getId(),
        StudentMapper.INSTANCE.toDomain(entity.getStudent()),
        ActivityMapper.INSTANCE.toDomain(entity.getActivity()),
        entity.getStartedAt(),
        entity.getReflection(),
        entity.getStartDate(),
        entity.getEndDate(),
        entity.getFinishedAt(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }

  @Override
  public DeclaredActivity toDomain(DeclaredActivityEntity entity, EntityGrapher<?> graph) {
    var attributs = graph.attributes();
    return DeclaredActivity.toDomain(
        entity.getId(),
        attributs.contains("student")
            ? StudentMapper.INSTANCE.toDomain(entity.getStudent(), graph.from("student"))
            : null,
        attributs.contains("activity")
            ? ActivityMapper.INSTANCE.toDomain(entity.getActivity(), graph.from("activity"))
            : null,
        entity.getStartedAt(),
        entity.getReflection(),
        entity.getStartDate(),
        entity.getEndDate(),
        entity.getFinishedAt(),
        entity.getCreatedAt(),
        entity.getUpdatedAt());
  }
}
